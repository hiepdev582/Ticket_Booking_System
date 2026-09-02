package com.ticketbooking.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto implements Serializable {
    private String id;
    private String bookingId;
    private String ticketNumber;
    private String passengerName;
    private String passengerPhone;
    private String tripCode;
    private String routeName;
    private String departureLocation;
    private String arrivalLocation;
    private LocalDateTime departureTime;
    private String seatNumber;
    private BigDecimal price;
    private String qrCodeUrl;
    private String pdfDownloadUrl;
    private LocalDateTime issuedAt;
}
