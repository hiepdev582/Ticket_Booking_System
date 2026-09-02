package com.ticketbooking.booking.entity;

import com.ticketbooking.common.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"trip_id", "seat_number"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripSeat {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "trip_id", nullable = false, length = 36)
    private String tripId;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Column(nullable = false)
    private int deck;

    @Column(nullable = false, length = 30)
    private String seatType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SeatStatus status = SeatStatus.AVAILABLE;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(length = 36)
    private String heldByUserId;

    private LocalDateTime heldUntil;

    @Version
    @Builder.Default
    private Integer version = 0;
}
