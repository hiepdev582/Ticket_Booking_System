package com.ticketbooking.ticket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 36)
    private String bookingId;

    @Column(unique = true, nullable = false, length = 50)
    private String ticketNumber;

    private String passengerName;
    private String passengerPhone;
    private String tripCode;
    private String routeName;
    private String departureLocation;
    private String arrivalLocation;
    private LocalDateTime departureTime;
    private String seatNumber;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(length = 1000)
    private String qrCodeUrl;

    @Column(nullable = false, length = 500)
    private String pdfStoragePath;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime issuedAt = LocalDateTime.now();
}
