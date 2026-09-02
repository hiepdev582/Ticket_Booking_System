package com.ticketbooking.common.dto;

import com.ticketbooking.common.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDto implements Serializable {
    private String id;
    private String tripCode;
    private String operatorName;
    private String routeName;
    private String departureLocation;
    private String arrivalLocation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private VehicleType vehicleType;
    private int totalSeats;
    private int availableSeats;
    private BigDecimal price;
    private List<TripSeatDto> seats;
}
