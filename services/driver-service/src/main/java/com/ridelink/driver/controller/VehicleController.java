package com.ridelink.driver.controller;

import com.ridelink.driver.dto.CreateVehicleRequest;
import com.ridelink.driver.dto.UpdateVehicleRequest;
import com.ridelink.driver.dto.VehicleResponse;
import com.ridelink.driver.model.VehicleType;
import com.ridelink.driver.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // POST /api/vehicles — Register a new vehicle for a driver
    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody CreateVehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.createVehicle(request));
    }

    // GET /api/vehicles — List all vehicles (supports filtering by vehicleType and activeOnly)
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAll(
            @RequestParam(required = false) VehicleType vehicleType,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly
    ) {
        return ResponseEntity.ok(vehicleService.getAllVehicles(vehicleType, activeOnly));
    }

    // GET /api/vehicles/{id} — Get vehicle by ID
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    // GET /api/vehicles/driver/{driverId} — Get vehicles assigned to a specific driver
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<VehicleResponse>> getByDriverId(@PathVariable String driverId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByDriverId(driverId));
    }

    // PUT /api/vehicles/{id} — Update vehicle details
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable String id,
            @RequestBody UpdateVehicleRequest request
    ) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }

    // DELETE /api/vehicles/{id} — Delete vehicle
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
