package com.ticketbooking.search.repository;

import com.ticketbooking.search.entity.TripSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripSeatRepository extends JpaRepository<TripSeat, String> {
    List<TripSeat> findByTripIdOrderByDeckAscSeatNumberAsc(String tripId);
}
