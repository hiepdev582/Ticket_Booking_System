package com.ticketbooking.payment.repository;

import com.ticketbooking.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
    Optional<PaymentTransaction> findByIdempotencyKey(String idempotencyKey);
    Optional<PaymentTransaction> findByBookingId(String bookingId);
}
