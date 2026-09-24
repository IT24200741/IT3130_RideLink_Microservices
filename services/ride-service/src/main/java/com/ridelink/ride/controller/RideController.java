package com.ridelink.ride.controller;

import com.ridelink.ride.dto.ApiResponse;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.dto.UpdateStatusRequest;
import com.ridelink.ride.service.RideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")

public class RideController {

    private final RideService rideService;

    @Autowired
    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

      @PostMapping
    public ResponseEntity<ApiResponse<RideResponse>> createRide(@Valid @RequestBody CreateRideRequest request) {
        RideResponse createdRide = rideService.createRide(request);
        return new ResponseEntity<>(
            ApiResponse.success("Ride requested successfully", createdRide),
            HttpStatus.CREATED
        );
    }

       @GetMapping
    public ResponseEntity<ApiResponse<List<RideResponse>>> getAllRides() {
        List<RideResponse> rides = rideService.getAllRides();
        return ResponseEntity.ok(ApiResponse.success(rides));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RideResponse>> getRideById(@PathVariable Long id) {
        RideResponse ride = rideService.getRideById(id);
        return ResponseEntity.ok(ApiResponse.success(ride));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RideResponse>> updateRideStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        RideResponse updatedRide = rideService.updateRideStatus(id, request.getStatus());
        return ResponseEntity.ok(
            ApiResponse.success("Ride status updated successfully", updatedRide)
        );
    }
}
