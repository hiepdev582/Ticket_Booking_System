package com.ticketbooking.common.dto;

import com.ticketbooking.common.enums.PaymentStatus;
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
public class PaymentResponse implements Serializable {
    private String transactionId;
    private String bookingId;
    private String bookingCode;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentMethod;
    private String message;
    private LocalDateTime paidAt;
}
