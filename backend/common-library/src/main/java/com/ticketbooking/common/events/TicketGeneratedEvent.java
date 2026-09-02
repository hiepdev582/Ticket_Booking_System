package com.ticketbooking.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketGeneratedEvent implements Serializable {
    private String ticketId;
    private String bookingId;
    private String ticketNumber;
    private String pdfStoragePath;
    private String qrCodeUrl;
    private String downloadUrl;
    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();
}
