package com.ridelink.payment.controller;

import com.ridelink.payment.dto.FareDetailResponse;
import com.ridelink.payment.dto.FareEstimateRequest;
import com.ridelink.payment.dto.FareResponse;
import com.ridelink.payment.dto.FinalFareRequest;
import com.ridelink.payment.service.FareService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fares")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    // POST /api/fares/estimate — Calculate initial estimated fare
    @PostMapping("/estimate")
    public ResponseEntity<FareResponse> estimateFare(@Valid @RequestBody FareEstimateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fareService.calculateFare(request));
    }

    // POST /api/fares/final — Calculate actual final fare upon ride completion
    @PostMapping("/final")
    public ResponseEntity<FareDetailResponse> calculateFinalFare(@Valid @RequestBody FinalFareRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fareService.calculateFinalFare(request));
    }

    // GET /api/fares/ride/{rideId} — Get fare record for a ride
    @GetMapping("/ride/{rideId}")
    public ResponseEntity<FareDetailResponse> getFareByRideId(@PathVariable String rideId) {
        return ResponseEntity.ok(fareService.getFareByRideId(rideId));
    }

    // GET /api/fares/{id} — Get fare record by ID
    @GetMapping("/{id}")
    public ResponseEntity<FareDetailResponse> getFareById(@PathVariable String id) {
        return ResponseEntity.ok(fareService.getFareById(id));
    }
}