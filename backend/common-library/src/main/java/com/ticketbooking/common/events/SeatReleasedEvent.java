package com.ticketbooking.common.events;

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
public class SeatReleasedEvent implements Serializable {
    private String bookingId;
    private String tripId;
    private List<String> seatNumbers;
    private String reason; // EXPIRED, CANCELLED
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
