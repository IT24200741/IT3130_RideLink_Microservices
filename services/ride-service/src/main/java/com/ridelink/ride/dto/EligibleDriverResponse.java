package com.ridelink.ride.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EligibleDriverResponse(
    String driverId,
    String driverName,
    String phone,
    Double distanceKm
) {}