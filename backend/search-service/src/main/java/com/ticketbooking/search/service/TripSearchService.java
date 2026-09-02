package com.ticketbooking.search.service;

import com.ticketbooking.common.dto.TripDto;
import com.ticketbooking.common.dto.TripSeatDto;
import com.ticketbooking.common.exception.ResourceNotFoundException;
import com.ticketbooking.search.entity.Trip;
import com.ticketbooking.search.entity.TripSeat;
import com.ticketbooking.search.repository.TripRepository;
import com.ticketbooking.search.repository.TripSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripSearchService {

    private final TripRepository tripRepository;
    private final TripSeatRepository tripSeatRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "trip_catalog", key = "'search:' + #departure + ':' + #arrival + ':' + #date")
    public List<TripDto> searchTrips(String departure, String arrival, LocalDate date) {
        log.info("Fetching trips from Database (Cache Miss): dep={}, arr={}, date={}", departure, arrival, date);
        LocalDateTime searchDate = (date != null) ? date.atStartOfDay() : null;
        List<Trip> trips = tripRepository.searchTrips(departure, arrival, searchDate);

        return trips.stream()
                .map(this::mapToTripDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "trip_details", key = "#id")
    public TripDto getTripById(String id) {
        log.info("Fetching trip details from Database: {}", id);
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi với ID: " + id));
        return mapToTripDto(trip);
    }

    @Transactional(readOnly = true)
    public List<TripSeatDto> getTripSeats(String tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new ResourceNotFoundException("Không tìm thấy chuyến đi với ID: " + tripId);
        }
        List<TripSeat> seats = tripSeatRepository.findByTripIdOrderByDeckAscSeatNumberAsc(tripId);
        return seats.stream()
                .map(this::mapToTripSeatDto)
                .collect(Collectors.toList());
    }

    private TripDto mapToTripDto(Trip trip) {
        return TripDto.builder()
                .id(trip.getId())
                .tripCode(trip.getTripCode())
                .operatorName(trip.getOperatorName())
                .routeName(trip.getRouteName())
                .departureLocation(trip.getDepartureLocation())
                .arrivalLocation(trip.getArrivalLocation())
                .departureTime(trip.getDepartureTime())
                .arrivalTime(trip.getArrivalTime())
                .vehicleType(trip.getVehicleType())
                .totalSeats(trip.getTotalSeats())
                .availableSeats(trip.getAvailableSeats())
                .price(trip.getPrice())
                .build();
    }

    private TripSeatDto mapToTripSeatDto(TripSeat seat) {
        return TripSeatDto.builder()
                .id(seat.getId())
                .tripId(seat.getTrip().getId())
                .seatNumber(seat.getSeatNumber())
                .deck(seat.getDeck())
                .seatType(seat.getSeatType())
                .status(seat.getStatus())
                .price(seat.getPrice())
                .heldByUserId(seat.getHeldByUserId())
                .heldUntil(seat.getHeldUntil())
                .build();
    }
}
