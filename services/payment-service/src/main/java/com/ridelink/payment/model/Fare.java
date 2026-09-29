package com.ridelink.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "fares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fare {

    @Id
    private String id;

    private String rideId;
    private double distanceKm;
    private double durationMinutes;
    private double baseFare;
    private double distanceCharge;
    private double timeCharge;

    @Builder.Default
    private double surgeMultiplier = 1.0;

    @Builder.Default
    private double tollCharges = 0.0;

    @Builder.Default
    private double discountAmount = 0.0;

    private double totalFare;

    @Builder.Default
    private String currency = "LKR";

    @Builder.Default
    private String fareType = "ESTIMATE"; // "ESTIMATE" or "FINAL"

    private LocalDateTime calculatedAt;
}