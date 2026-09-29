package com.ridelink.driver.controller;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.model.Availability;
import com.ridelink.driver.model.VehicleType;
import com.ridelink.driver.service.DriverProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverProfileController {

    private final DriverProfileService service;

    public DriverProfileController(DriverProfileService service) {
        this.service = service;
    }

    // POST /api/drivers — Register / Create a driver profile
    @PostMapping
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody CreateDriverRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    // GET /api/drivers — List all drivers (supports filtering by availability & service area)
    @GetMapping
    public ResponseEntity<List<DriverResponse>> getAll(
            @RequestParam(required = false) Availability availability,
            @RequestParam(required = false) String serviceArea
    ) {
        return ResponseEntity.ok(service.getAll(availability, serviceArea));
    }

    // GET /api/drivers/{id} — Get driver by ID
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /api/drivers/account/{accountId} — Get driver by Account ID
    @GetMapping("/account/{accountId}")
    public ResponseEntity<DriverResponse> getByAccountId(@PathVariable String accountId) {
        return ResponseEntity.ok(service.getByAccountId(accountId));
    }

    // PUT /api/drivers/{id} — Update driver details
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> update(
            @PathVariable String id,
            @RequestBody UpdateDriverRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    // PUT /api/drivers/{id}/availability — Update driver availability status
    @PutMapping("/{id}/availability")
    public ResponseEntity<DriverResponse> updateAvailability(
            @PathVariable String id,
            @Valid @RequestBody UpdateAvailabilityRequest request
    ) {
        return ResponseEntity.ok(service.updateAvailability(id, request));
    }

    // PUT /api/drivers/{id}/location — Update driver live GPS location
    @PutMapping("/{id}/location")
    public ResponseEntity<DriverResponse> updateLocation(
            @PathVariable String id,
            @Valid @RequestBody UpdateLocationRequest request
    ) {
        return ResponseEntity.ok(service.updateLocation(id, request));
    }

    // GET /api/drivers/eligible — Find eligible nearby available drivers for dispatch
    @GetMapping("/eligible")
    public ResponseEntity<List<EligibleDriverResponse>> findEligibleDrivers(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm,
            @RequestParam(required = false) VehicleType vehicleType,
            @RequestParam(required = false) String serviceArea
    ) {
        return ResponseEntity.ok(service.findEligibleDrivers(latitude, longitude, radiusKm, vehicleType, serviceArea));
    }

    // DELETE /api/drivers/{id} — Delete driver profile
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}