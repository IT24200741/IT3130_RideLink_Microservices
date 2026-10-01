package com.ridelink.ride.controller;

import com.ridelink.ride.dto.ApiResponse;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.dto.UpdateStatusRequest;
import com.ridelink.ride.security.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @Autowired
    public RideController(RideService rideService, JwtUtil jwtUtil) {
        this.rideService = rideService;
        this.jwtUtil = jwtUtil;
    }

    private record CallerContext(String userId, String role) {}
    private CallerContext extractCaller(String authHeader, String headerUserId, String headerRole) {
        String userId = headerUserId;
        String role = headerRole;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String jwtUser = jwtUtil.extractUserId(token);
            String jwtRole = jwtUtil.extractRole(token);
            if (jwtUser != null) userId = jwtUser;
            if (jwtRole != null) role = jwtRole;
        }
        return new CallerContext(userId, role);
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
    public ResponseEntity<ApiResponse<List<RideResponse>>> getAllRides(
            @RequestParam(required = false) String passengerId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId,
            @RequestHeader(value = "X-User-Role", required = false) String headerRole) {
        CallerContext caller = extractCaller(authHeader, headerUserId, headerRole);
        String targetPassengerId = (passengerId != null && !passengerId.isBlank()) ? passengerId : caller.userId();
        List<RideResponse> rides = rideService.getRides(targetPassengerId, caller.role());
        return ResponseEntity.ok(ApiResponse.success(rides));
    }

     @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RideResponse>> getRideById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId,
            @RequestHeader(value = "X-User-Role", required = false) String headerRole) {
        CallerContext caller = extractCaller(authHeader, headerUserId, headerRole);
        RideResponse ride = rideService.getRideById(id, caller.userId(), caller.role());
        return ResponseEntity.ok(ApiResponse.success(ride));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RideResponse>> updateRideStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId,
            @RequestHeader(value = "X-User-Role", required = false) String headerRole) {
        CallerContext caller = extractCaller(authHeader, headerUserId, headerRole);
        RideResponse updatedRide = rideService.updateRideStatus(id, request.getStatus(), caller.userId(), caller.role());
        return ResponseEntity.ok(
            ApiResponse.success("Ride status updated successfully", updatedRide)
        );
    }

        @PostMapping("/{id}/assign-driver")
    public ResponseEntity<ApiResponse<RideResponse>> assignDriver(@PathVariable Long id) {
        RideResponse assignedRide = rideService.assignDriver(id);
        return ResponseEntity.ok(
            ApiResponse.success("Driver assigned successfully", assignedRide)
        );
    }
}
