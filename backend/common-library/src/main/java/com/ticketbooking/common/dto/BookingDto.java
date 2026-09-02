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
public class BookingDto implements Serializable {
    private String id;
    private String bookingCode;
    private String userId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String tripId;
    private String tripCode;
    private String routeName;
    private LocalDateTime departureTime;
    private List<String> seatNumbers;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
