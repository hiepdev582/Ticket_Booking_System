package com.ticketbooking.ticket.service;

import com.ticketbooking.common.constants.KafkaTopicConstants;
import com.ticketbooking.common.dto.TicketDto;
import com.ticketbooking.common.events.OrderPaidEvent;
import com.ticketbooking.common.events.TicketGeneratedEvent;
import com.ticketbooking.common.exception.ResourceNotFoundException;
import com.ticketbooking.ticket.entity.Ticket;
import com.ticketbooking.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PdfTicketGenerator pdfTicketGenerator;
    private final MinioStorageService minioStorageService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopicConstants.ORDER_PAID_TOPIC, groupId = "ticket-service-group")
    @Transactional
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("TicketService: Nhận OrderPaidEvent đơn #{}, tiến hành render PDF vé và upload MinIO",
                event.getBookingCode());

        if (event.getSeatNumbers() == null || event.getSeatNumbers().isEmpty()) {
            log.warn("TicketService: Bỏ qua tạo vé do danh sách ghế rỗng cho đơn #{}", event.getBookingCode());
            return;
        }

        List<String> seats = event.getSeatNumbers();

        List<Ticket> generatedTickets = new ArrayList<>();

        for (String seatNumber : seats) {
            String ticketId = UUID.randomUUID().toString();
            String ticketNumber = "TCK-" + System.currentTimeMillis() % 1000000 + "-" + seatNumber;
            String pdfStoragePath = String.format("tickets/%s/%s.pdf", event.getBookingId(), seatNumber);

            String tripCodeVal = event.getTripCode() != null && !event.getTripCode().isBlank()
                    ? event.getTripCode()
                    : (event.getTripId() != null ? event.getTripId() : "TRIP-UNKNOWN");

            String routeNameVal = event.getRouteName() != null && !event.getRouteName().isBlank()
                    ? event.getRouteName()
                    : "Hành trình xe khách / tàu hỏa";

            LocalDateTime depTimeVal = event.getDepartureTime() != null
                    ? event.getDepartureTime()
                    : LocalDateTime.now().plusDays(1);

            Ticket ticket = Ticket.builder()
                    .id(ticketId)
                    .bookingId(event.getBookingId())
                    .ticketNumber(ticketNumber)
                    .passengerName(event.getCustomerName() != null && !event.getCustomerName().isBlank()
                            ? event.getCustomerName()
                            : "Khách hàng")
                    .passengerPhone(event.getCustomerPhone())
                    .tripCode(tripCodeVal)
                    .routeName(routeNameVal)
                    .departureTime(depTimeVal)
                    .seatNumber(seatNumber)
                    .price(event.getAmount())
                    .pdfStoragePath(pdfStoragePath)
                    .qrCodeUrl("QR:" + ticketNumber)
                    .issuedAt(LocalDateTime.now())
                    .build();

            // 1. Render PDF bằng OpenPDF & ZXing
            byte[] pdfBytes = pdfTicketGenerator.generateTicketPdf(ticket);

            // 2. Upload file PDF vé điện tử lên MinIO S3
            minioStorageService.uploadFile(pdfStoragePath, pdfBytes, "application/pdf");

            ticketRepository.save(ticket);
            generatedTickets.add(ticket);

            // 3. Bắn event TicketGeneratedEvent sang Kafka
            String presignedUrl = minioStorageService.getPresignedDownloadUrl(pdfStoragePath, 60);
            TicketGeneratedEvent generatedEvent = TicketGeneratedEvent.builder()
                    .ticketId(ticketId)
                    .bookingId(event.getBookingId())
                    .ticketNumber(ticketNumber)
                    .pdfStoragePath(pdfStoragePath)
                    .downloadUrl(presignedUrl)
                    .build();
            kafkaTemplate.send(KafkaTopicConstants.TICKET_GENERATED_TOPIC, ticketId, generatedEvent);
        }

        log.info("TicketService: Đã xuất thành công {} vé điện tử cho đơn hàng #{}", generatedTickets.size(),
                event.getBookingCode());
    }

    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsByBookingId(String bookingId) {
        List<Ticket> tickets = ticketRepository.findByBookingId(bookingId);
        return tickets.stream()
                .map(this::mapToTicketDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketDto getTicketById(String ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé với ID: " + ticketId));
        return mapToTicketDto(ticket);
    }

    public byte[] downloadTicketPdf(String ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé với ID: " + ticketId));
        return pdfTicketGenerator.generateTicketPdf(ticket);
    }

    private TicketDto mapToTicketDto(Ticket ticket) {
        String downloadUrl = minioStorageService.getPresignedDownloadUrl(ticket.getPdfStoragePath(), 60);
        return TicketDto.builder()
                .id(ticket.getId())
                .bookingId(ticket.getBookingId())
                .ticketNumber(ticket.getTicketNumber())
                .passengerName(ticket.getPassengerName())
                .passengerPhone(ticket.getPassengerPhone())
                .tripCode(ticket.getTripCode())
                .routeName(ticket.getRouteName())
                .departureTime(ticket.getDepartureTime())
                .seatNumber(ticket.getSeatNumber())
                .price(ticket.getPrice())
                .qrCodeUrl(ticket.getQrCodeUrl())
                .pdfDownloadUrl(downloadUrl)
                .issuedAt(ticket.getIssuedAt())
                .build();
    }
}
