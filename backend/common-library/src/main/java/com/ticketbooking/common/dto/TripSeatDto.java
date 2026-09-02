package com.ticketbooking.common.dto;

import com.ticketbooking.common.enums.SeatStatus;
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
public class TripSeatDto implements Serializable {
    private String id;
    private String tripId;
    private String seatNumber;
    private int deck; // 1: Tầng dưới (Lower), 2: Tầng trên (Upper)
    private String seatType; // VIP, STANDARD, SLEEPER
    private SeatStatus status;
    private BigDecimal price;
    private String heldByUserId;
    private LocalDateTime heldUntil;
}
