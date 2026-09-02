package com.ticketbooking.payment.entity;

import com.ticketbooking.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 36)
    private String bookingId;

    @Column(length = 100)
    private String idempotencyKey;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false, length = 30)
    private String paymentMethod; // VNPAY, MOMO, CREDIT_CARD

    private String responseMessage;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
