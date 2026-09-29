package com.ridelink.payment.dto;

public record FareResponse(
        String id,
        String rideId,
        double distanceKm,
        double durationMinutes,
        double baseFare,
        double distanceCharge,
        double timeCharge,
        double totalFare,
        String currency
) {
}