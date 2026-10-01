package com.ridelink.payment.dto;

import com.ridelink.payment.model.Fare;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FareDetailResponse {

    private String id;
    private String rideId;
    private double distanceKm;
    private double durationMinutes;
    private double baseFare;
    private double distanceCharge;
    private double timeCharge;
    private double surgeMultiplier;
    private double tollCharges;
    private double discountAmount;
    private double totalFare;
    private String currency;
    private String fareType;
    private LocalDateTime calculatedAt;

    public static FareDetailResponse fromEntity(Fare fare) {
        if (fare == null) return null;
        return FareDetailResponse.builder()
                .id(fare.getId())
                .rideId(fare.getRideId())
                .distanceKm(fare.getDistanceKm())
                .durationMinutes(fare.getDurationMinutes())
                .baseFare(fare.getBaseFare())
                .distanceCharge(fare.getDistanceCharge())
                .timeCharge(fare.getTimeCharge())
                .surgeMultiplier(fare.getSurgeMultiplier())
                .tollCharges(fare.getTollCharges())
                .discountAmount(fare.getDiscountAmount())
                .totalFare(fare.getTotalFare())
                .currency(fare.getCurrency())
                .fareType(fare.getFareType())
                .calculatedAt(fare.getCalculatedAt())
                .build();
    }
}
