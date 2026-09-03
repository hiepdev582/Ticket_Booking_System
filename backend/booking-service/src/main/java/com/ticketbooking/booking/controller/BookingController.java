package com.ticketbooking.booking.controller;

import com.ticketbooking.booking.service.BookingService;
import com.ticketbooking.common.dto.ApiResponse;
import com.ticketbooking.common.dto.BookingDto;
import com.ticketbooking.common.dto.HoldSeatRequest;
import com.ticketbooking.common.dto.HoldSeatResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/hold")
    public ResponseEntity<ApiResponse<HoldSeatResponse>> holdSeats(@Valid @RequestBody HoldSeatRequest request) {
        HoldSeatResponse response = bookingService.holdSeats(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Giữ ghế thành công trong 5 phút!", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingDto>> getBookingDetails(@PathVariable(name = "id") String id) {
        BookingDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy thông tin đơn đặt thành công", booking));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(@PathVariable(name = "id") String id) {
        bookingService.releaseSeatsForBooking(id, "USER_CANCELLED");
        return ResponseEntity.ok(ApiResponse.ok("Hủy đơn giữ chỗ thành công", null));
    }
}
