package com.ticketbooking.search.controller;

import com.ticketbooking.common.dto.ApiResponse;
import com.ticketbooking.common.dto.TripDto;
import com.ticketbooking.common.dto.TripSeatDto;
import com.ticketbooking.search.service.TripSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripSearchController {

    private final TripSearchService tripSearchService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TripDto>>> searchTrips(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String arrival,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<TripDto> trips = tripSearchService.searchTrips(departure, arrival, date);
        return ResponseEntity.ok(ApiResponse.ok("Tìm kiếm chuyến đi thành công", trips));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TripDto>> getTripDetails(@PathVariable String id) {
        TripDto trip = tripSearchService.getTripById(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy chi tiết chuyến đi thành công", trip));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<ApiResponse<List<TripSeatDto>>> getTripSeats(@PathVariable String id) {
        List<TripSeatDto> seats = tripSearchService.getTripSeats(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy sơ đồ ghế thành công", seats));
    }
}
