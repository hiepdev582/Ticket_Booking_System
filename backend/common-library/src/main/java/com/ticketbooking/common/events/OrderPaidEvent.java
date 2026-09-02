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
public class OrderPaidEvent implements Serializable {
    private String transactionId;
    private String bookingId;
    private String bookingCode;
    private String tripId;
    private String userId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private List<String> seatNumbers;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime paidAt;
}
