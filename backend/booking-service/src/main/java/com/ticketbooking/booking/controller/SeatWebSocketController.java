package com.ticketbooking.booking.controller;

import com.ticketbooking.common.dto.SeatUpdateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SeatWebSocketController {

    /**
     * Nhận tin nhắn WebSocket từ client và broadcast tới tất cả các client khác đang xem cùng trip
     */
    @MessageMapping("/trips/{tripId}/ping")
    @SendTo("/topic/trips/{tripId}/seats")
    public SeatUpdateMessage handleClientPing(@DestinationVariable String tripId, SeatUpdateMessage message) {
        log.debug("Nhận WebSocket Ping từ client cho chuyến {}: {}", tripId, message);
        return message;
    }
}
