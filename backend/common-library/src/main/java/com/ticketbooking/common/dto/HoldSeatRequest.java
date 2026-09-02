package com.ticketbooking.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldSeatRequest implements Serializable {
    @NotBlank(message = "Trip ID is required")
    private String tripId;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String customerName;
    private String customerPhone;
    private String customerEmail;

    private List<String> seatNumbers;
}
