package com.ridelink.payment.service;

import com.ridelink.payment.dto.FareDetailResponse;
import com.ridelink.payment.dto.FareEstimateRequest;
import com.ridelink.payment.dto.FareResponse;
import com.ridelink.payment.dto.FinalFareRequest;
import com.ridelink.payment.exception.FareNotFoundException;
import com.ridelink.payment.model.Fare;
import com.ridelink.payment.repository.FareRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FareService {

    private final FareRepository fareRepository;

    private static final double BASE_FARE = 100.00;
    private static final double DISTANCE_RATE_PER_KM = 50.00;
    private static final double TIME_RATE_PER_MIN = 5.00;

    public FareService(FareRepository fareRepository) {
        this.fareRepository = fareRepository;
    }

    /**
     * Calculate initial estimated fare for a ride request.
     */
    public FareResponse calculateFare(FareEstimateRequest request) {
        double distanceCharge = request.distanceKm() * DISTANCE_RATE_PER_KM;
        double timeCharge = request.durationMinutes() * TIME_RATE_PER_MIN;
        double totalFare = BASE_FARE + distanceCharge + timeCharge;

        Fare fare = Fare.builder()
                .rideId(request.rideId())
                .distanceKm(request.distanceKm())
                .durationMinutes(request.durationMinutes())
                .baseFare(BASE_FARE)
                .distanceCharge(distanceCharge)
                .timeCharge(timeCharge)
                .surgeMultiplier(1.0)
                .tollCharges(0.0)
                .discountAmount(0.0)
                .totalFare(Math.round(totalFare * 100.0) / 100.0)
                .currency("LKR")
                .fareType("ESTIMATE")
                .calculatedAt(LocalDateTime.now())
                .build();

        Fare savedFare = fareRepository.save(fare);

        return new FareResponse(
                savedFare.getId(),
                savedFare.getRideId(),
                savedFare.getDistanceKm(),
                savedFare.getDurationMinutes(),
                savedFare.getBaseFare(),
                savedFare.getDistanceCharge(),
                savedFare.getTimeCharge(),
                savedFare.getTotalFare(),
                savedFare.getCurrency()
        );
    }

    /**
     * Calculate actual final fare upon ride completion with tolls, discounts, and surge multiplier.
     */
    public FareDetailResponse calculateFinalFare(FinalFareRequest request) {
        double distanceCharge = request.getActualDistanceKm() * DISTANCE_RATE_PER_KM;
        double timeCharge = request.getActualDurationMinutes() * TIME_RATE_PER_MIN;

        double surge = (request.getSurgeMultiplier() != null && request.getSurgeMultiplier() > 0)
                ? request.getSurgeMultiplier() : 1.0;
        double tolls = (request.getTollCharges() != null && request.getTollCharges() >= 0)
                ? request.getTollCharges() : 0.0;
        double discount = (request.getDiscountAmount() != null && request.getDiscountAmount() >= 0)
                ? request.getDiscountAmount() : 0.0;

        double subtotal = (BASE_FARE + distanceCharge + timeCharge) * surge;
        double totalFare = Math.max(0, subtotal + tolls - discount);

        Fare fare = Fare.builder()
                .rideId(request.getRideId())
                .distanceKm(request.getActualDistanceKm())
                .durationMinutes(request.getActualDurationMinutes())
                .baseFare(BASE_FARE)
                .distanceCharge(distanceCharge)
                .timeCharge(timeCharge)
                .surgeMultiplier(surge)
                .tollCharges(tolls)
                .discountAmount(discount)
                .totalFare(Math.round(totalFare * 100.0) / 100.0)
                .currency("LKR")
                .fareType("FINAL")
                .calculatedAt(LocalDateTime.now())
                .build();

        Fare savedFare = fareRepository.save(fare);
        return FareDetailResponse.fromEntity(savedFare);
    }

    public FareDetailResponse getFareByRideId(String rideId) {
        Fare fare = fareRepository.findByRideId(rideId)
                .orElseThrow(() -> new FareNotFoundException("Fare record not found for ride ID: " + rideId));
        return FareDetailResponse.fromEntity(fare);
    }

    public FareDetailResponse getFareById(String id) {
        Fare fare = fareRepository.findById(id)
                .orElseThrow(() -> new FareNotFoundException("Fare record not found with ID: " + id));
        return FareDetailResponse.fromEntity(fare);
    }
}