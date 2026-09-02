package com.ticketbooking.ticket.repository;

import com.ticketbooking.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    List<Ticket> findByBookingId(String bookingId);
}
