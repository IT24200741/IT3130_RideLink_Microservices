package com.ridelink.ride.controller;

import com.ridelink.ride.dto.ApiResponse;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.dto.UpdateStatusRequest;
import com.ridelink.ride.exception.UnauthorizedAccessException;
import com.ridelink.ride.security.JwtUtil;
import com.ridelink.ride.service.RideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/rides")
@Tag(name = "Ride Management Service", description = "Endpoints for requesting, assigning drivers, and tracking ride lifecycle states")
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

        @Operation(summary = "Request a new ride", description = "Creates a ride in REQUESTED status with auto-calculated Haversine distance")
    @PostMapping
    public ResponseEntity<ApiResponse<RideResponse>> createRide(@Valid @RequestBody CreateRideRequest request) {
        RideResponse createdRide = rideService.createRide(request);
        return new ResponseEntity<>(
            ApiResponse.success("Ride requested successfully", createdRide),
            HttpStatus.CREATED
        );
    }

    @Operation(summary = "Get all rides", description = "Retrieves rides filtered by caller role (passengers see own rides, drivers see assigned rides, admin sees all)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RideResponse>>> getAllRides(
            @RequestParam(required = false) String passengerId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId,
            @RequestHeader(value = "X-User-Role", required = false) String headerRole) {
        CallerContext caller = extractCaller(authHeader, headerUserId, headerRole);

        
        if ((caller.userId() == null || caller.userId().isBlank()) && (caller.role() == null || caller.role().isBlank())) {
            throw new UnauthorizedAccessException("Authentication required to view rides.");
        }

        
        if ("PASSENGER".equalsIgnoreCase(caller.role())) {
            if (passengerId != null && !passengerId.isBlank() && !passengerId.equals(caller.userId())) {
                throw new UnauthorizedAccessException("Passengers are only permitted to view their own rides.");
            }
        }

        String targetPassengerId = ("ADMIN".equalsIgnoreCase(caller.role()) && passengerId != null && !passengerId.isBlank()) 
                ? passengerId : caller.userId();

        List<RideResponse> rides = rideService.getRides(targetPassengerId, caller.role());
        return ResponseEntity.ok(ApiResponse.success(rides));
    }
         @Operation(summary = "Get ride by ID", description = "Fetches ride details with passenger ownership verification")
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

        @Operation(summary = "Update ride status", description = "Advances the ride status following lifecycle state machine rules")
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

            @Operation(summary = "Assign eligible driver", description = "Synchronously queries Driver Service via RestClient and assigns the nearest driver")
    @PostMapping("/{id}/assign-driver")
    public ResponseEntity<ApiResponse<RideResponse>> assignDriver(@PathVariable Long id) {
        RideResponse assignedRide = rideService.assignDriver(id);
        return ResponseEntity.ok(
            ApiResponse.success("Driver assigned successfully", assignedRide)
        );
    }
}
