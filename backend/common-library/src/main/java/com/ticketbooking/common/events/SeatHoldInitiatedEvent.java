package com.ticketbooking.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatHoldInitiatedEvent implements Serializable {
    private String bookingId;
    private String bookingCode;
    private String tripId;
    private String userId;
    private List<String> seatNumbers;
    private BigDecimal totalAmount;
    private LocalDateTime expiresAt;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
