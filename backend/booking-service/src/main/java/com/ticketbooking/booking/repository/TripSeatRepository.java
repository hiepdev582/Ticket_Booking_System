package com.ticketbooking.booking.repository;

import com.ticketbooking.booking.entity.TripSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripSeatRepository extends JpaRepository<TripSeat, String> {
    Optional<TripSeat> findByTripIdAndSeatNumber(String tripId, String seatNumber);
    List<TripSeat> findByTripIdAndSeatNumberIn(String tripId, List<String> seatNumbers);
    List<TripSeat> findByTripIdOrderByDeckAscSeatNumberAsc(String tripId);
}
