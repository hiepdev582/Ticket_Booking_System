package com.ticketbooking.search.repository;

import com.ticketbooking.search.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, String> {

    @Query("SELECT t FROM Trip t WHERE " +
           "(:departureLocation IS NULL OR LOWER(t.departureLocation) LIKE LOWER(CONCAT('%', :departureLocation, '%'))) AND " +
           "(:arrivalLocation IS NULL OR LOWER(t.arrivalLocation) LIKE LOWER(CONCAT('%', :arrivalLocation, '%'))) AND " +
           "(:departureDate IS NULL OR t.departureTime >= :departureDate) " +
           "ORDER BY t.departureTime ASC")
    List<Trip> searchTrips(
            @Param("departureLocation") String departureLocation,
            @Param("arrivalLocation") String arrivalLocation,
            @Param("departureDate") LocalDateTime departureDate
    );
}
