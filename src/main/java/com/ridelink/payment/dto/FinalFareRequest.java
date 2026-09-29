package com.ridelink.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalFareRequest {

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @NotNull(message = "Actual distance in km is required")
    @DecimalMin(value = "0.1", message = "Distance must be greater than 0")
    private Double actualDistanceKm;

    @NotNull(message = "Actual duration in minutes is required")
    @DecimalMin(value = "1.0", message = "Duration must be at least 1 minute")
    private Double actualDurationMinutes;

    @Builder.Default
    private Double tollCharges = 0.0;

    @Builder.Default
    private Double discountAmount = 0.0;

    @Builder.Default
    private Double surgeMultiplier = 1.0;
}
