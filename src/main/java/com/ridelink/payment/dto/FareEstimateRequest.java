package com.ridelink.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FareEstimateRequest(

        @NotBlank(message = "Ride ID is required")
        String rideId,

        @NotNull(message = "Distance is required")
        @DecimalMin(value = "0.1", message = "Distance must be greater than 0")
        Double distanceKm,

        @NotNull(message = "Duration is required")
        @DecimalMin(value = "1", message = "Duration must be at least 1 minute")
        Double durationMinutes
) {
}