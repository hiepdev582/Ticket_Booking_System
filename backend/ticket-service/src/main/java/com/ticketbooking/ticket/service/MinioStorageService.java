package com.ticketbooking.ticket.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name:bucket-tickets}")
    private String bucketName;

    public void uploadFile(String objectName, byte[] data, String contentType) {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("MinIO Bucket '{}' được tạo mới thành công.", bucketName);
            }

            try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(bais, data.length, -1)
                                .contentType(contentType)
                                .build()
                );
                log.info("Upload file '{}' lên MinIO Bucket '{}' thành công!", objectName, bucketName);
            }
        } catch (Exception e) {
            log.error("Lỗi khi upload file '{}' lên MinIO: {}", objectName, e.getMessage());
            throw new RuntimeException("Lỗi lưu trữ MinIO", e);
        }
    }

    public InputStream getFileInputStream(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Lỗi khi tải file '{}' từ MinIO: {}", objectName, e.getMessage());
            throw new RuntimeException("Lỗi đọc file từ MinIO", e);
        }
    }

    public String getPresignedDownloadUrl(String objectName, int expiryMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiryMinutes, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Không thể tạo Presigned URL cho '{}': {}", objectName, e.getMessage());
            return "/api/v1/tickets/download/" + objectName;
        }
    }
}
