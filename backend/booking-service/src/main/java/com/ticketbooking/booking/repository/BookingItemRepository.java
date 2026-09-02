package com.ticketbooking.booking.repository;

import com.ticketbooking.booking.entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingItemRepository extends JpaRepository<BookingItem, String> {
    List<BookingItem> findByBookingId(String bookingId);
}
