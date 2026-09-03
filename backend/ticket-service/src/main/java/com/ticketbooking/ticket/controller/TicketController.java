package com.ticketbooking.ticket.controller;

import com.ticketbooking.common.dto.ApiResponse;
import com.ticketbooking.common.dto.TicketDto;
import com.ticketbooking.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<TicketDto>>> getTicketsByBooking(@PathVariable(name = "bookingId") String bookingId) {
        List<TicketDto> tickets = ticketService.getTicketsByBookingId(bookingId);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách vé thành công", tickets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDto>> getTicketDetails(@PathVariable(name = "id") String id) {
        TicketDto ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy thông tin vé thành công", ticket));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable(name = "id") String id) {
        byte[] pdfBytes = ticketService.downloadTicketPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
