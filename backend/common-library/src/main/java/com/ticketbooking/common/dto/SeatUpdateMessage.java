package com.ticketbooking.common.dto;

import com.ticketbooking.common.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatUpdateMessage implements Serializable {
    private String tripId;
    private List<String> seatNumbers;
    private SeatStatus status;
    private String userId; // User đang thực hiện thao tác
    private String eventType; // SEAT_HELD, SEAT_RELEASED, SEAT_BOOKED
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
