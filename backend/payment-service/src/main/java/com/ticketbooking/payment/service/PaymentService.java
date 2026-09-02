package com.ticketbooking.payment.service;

import com.ticketbooking.common.constants.KafkaTopicConstants;
import com.ticketbooking.common.dto.PaymentRequest;
import com.ticketbooking.common.dto.PaymentResponse;
import com.ticketbooking.common.enums.PaymentStatus;
import com.ticketbooking.common.events.OrderPaidEvent;
import com.ticketbooking.payment.entity.PaymentTransaction;
import com.ticketbooking.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Bắt đầu xử lý thanh toán cho đơn hàng #{}", request.getBookingId());

        // 1. Kiểm tra tính lũy thừa (Idempotency Key) tránh trừ tiền 2 lần
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            Optional<PaymentTransaction> existingTx = paymentTransactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existingTx.isPresent()) {
                PaymentTransaction tx = existingTx.get();
                log.info("Phát hiện giao dịch trùng lặp IdempotencyKey={}, trả về kết quả trước đó", request.getIdempotencyKey());
                return mapToResponse(tx, "Giao dịch đã được xử lý trước đó");
            }
        }

        // 2. Giả lập tích hợp cổng thanh toán (VNPay/MoMo Sandbox) thành công
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentTransaction transaction = PaymentTransaction.builder()
                .id(transactionId)
                .bookingId(request.getBookingId())
                .idempotencyKey(request.getIdempotencyKey())
                .amount(request.getAmount())
                .status(PaymentStatus.SUCCESS)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "VNPAY")
                .responseMessage("Thanh toán thành công qua cổng thanh toán điện tử")
                .createdAt(LocalDateTime.now())
                .build();

        paymentTransactionRepository.save(transaction);

        // 3. Phát sự kiện OrderPaidEvent vào Apache Kafka
        OrderPaidEvent event = OrderPaidEvent.builder()
                .transactionId(transactionId)
                .bookingId(request.getBookingId())
                .bookingCode("BK-" + transactionId.substring(4))
                .amount(request.getAmount())
                .paymentMethod(transaction.getPaymentMethod())
                .paidAt(transaction.getCreatedAt())
                .seatNumbers(Collections.emptyList()) // Sẽ được enrich tại consumer
                .build();

        kafkaTemplate.send(KafkaTopicConstants.ORDER_PAID_TOPIC, request.getBookingId(), event);
        log.info("Đã phát sự kiện OrderPaidEvent vào Kafka topic '{}' cho đơn #{}", KafkaTopicConstants.ORDER_PAID_TOPIC, request.getBookingId());

        return mapToResponse(transaction, "Thanh toán đơn hàng thành công!");
    }

    private PaymentResponse mapToResponse(PaymentTransaction tx, String message) {
        return PaymentResponse.builder()
                .transactionId(tx.getId())
                .bookingId(tx.getBookingId())
                .amount(tx.getAmount())
                .status(tx.getStatus())
                .paymentMethod(tx.getPaymentMethod())
                .message(message)
                .paidAt(tx.getCreatedAt())
                .build();
    }
}
