package com.ticketbooking.common.dto;

import com.ticketbooking.common.enums.BookingStatus;
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
public class HoldSeatResponse implements Serializable {
    private String bookingId;
    private String bookingCode;
    private String tripId;
    private String userId;
    private List<String> seatNumbers;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private LocalDateTime expiresAt;
    private long remainingSeconds; // Mặc định 300s (5 phút)
}
