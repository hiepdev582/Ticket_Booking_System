package com.ticketbooking.booking.repository;

import com.ticketbooking.booking.entity.Booking;
import com.ticketbooking.common.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, String> {
    Optional<Booking> findByBookingCode(String bookingCode);
    List<Booking> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, LocalDateTime now);
}
