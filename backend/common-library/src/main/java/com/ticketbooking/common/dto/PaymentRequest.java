package com.ticketbooking.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest implements Serializable {
    @NotBlank(message = "Booking ID is required")
    private String bookingId;

    private String bookingCode;
    private String tripId;
    private String tripCode;
    private String routeName;
    private String departureTime;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private java.util.List<String> seatNumbers;

    private String paymentMethod; // VNPAY, MOMO, CREDIT_CARD
    private BigDecimal amount;
    private String idempotencyKey;
}
