package com.ridelink.ride.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QueryEligibleDriversRequest(
    Double pickupLat,
    Double pickupLng,
    String vehicleType,
    Double maxRadiusKm
) {}