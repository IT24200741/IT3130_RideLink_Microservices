package com.ridelink.payment.service;

import com.ridelink.payment.dto.FareEstimateRequest;
import com.ridelink.payment.dto.FareResponse;
import com.ridelink.payment.model.Fare;
import com.ridelink.payment.repository.FareRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FareService {

    private final FareRepository fareRepository;

    public FareService(FareRepository fareRepository) {
        this.fareRepository = fareRepository;
    }

    public FareResponse calculateFare(FareEstimateRequest request) {

        // Temporary calculation rule.
        // We will replace/adjust this according to the
        // exact assignment fare specification.

        double baseFare = 100.00;
        double distanceRate = 50.00;
        double timeRate = 5.00;

        double distanceCharge =
                request.distanceKm() * distanceRate;

        double timeCharge =
                request.durationMinutes() * timeRate;

        double totalFare =
                baseFare + distanceCharge + timeCharge;

        Fare fare = Fare.builder()
                .rideId(request.rideId())
                .distanceKm(request.distanceKm())
                .durationMinutes(request.durationMinutes())
                .baseFare(baseFare)
                .distanceCharge(distanceCharge)
                .timeCharge(timeCharge)
                .totalFare(totalFare)
                .currency("LKR")
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
}