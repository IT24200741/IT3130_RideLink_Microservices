package com.ridelink.driver.service;

import com.ridelink.driver.dto.CreateVehicleRequest;
import com.ridelink.driver.dto.UpdateVehicleRequest;
import com.ridelink.driver.dto.VehicleResponse;
import com.ridelink.driver.exception.DriverNotFoundException;
import com.ridelink.driver.exception.DuplicateResourceException;
import com.ridelink.driver.exception.VehicleNotFoundException;
import com.ridelink.driver.model.DriverProfile;
import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.model.VehicleType;
import com.ridelink.driver.repository.DriverProfileRepository;
import com.ridelink.driver.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverProfileRepository driverProfileRepository;

    public VehicleService(VehicleRepository vehicleRepository, DriverProfileRepository driverProfileRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverProfileRepository = driverProfileRepository;
    }

    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        // Check if driver exists
        DriverProfile driver = driverProfileRepository.findById(request.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " + request.getDriverId()));

        // Check if license plate is already registered
        if (vehicleRepository.existsByLicensePlateIgnoreCase(request.getLicensePlate())) {
            throw new DuplicateResourceException("Vehicle with license plate '" + request.getLicensePlate() + "' is already registered");
        }

        Vehicle vehicle = Vehicle.builder()
                .driverId(request.getDriverId())
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .color(request.getColor())
                .licensePlate(request.getLicensePlate().toUpperCase().trim())
                .vehicleType(request.getVehicleType())
                .capacity(request.getCapacity())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);

        // Auto-assign as active vehicle for driver if driver has no active vehicle
        if (driver.getActiveVehicleId() == null) {
            driver.setActiveVehicleId(saved.getId());
            driver.setUpdatedAt(LocalDateTime.now());
            driverProfileRepository.save(driver);
        }

        return VehicleResponse.fromEntity(saved);
    }

    public VehicleResponse getVehicleById(String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with ID: " + id));
        return VehicleResponse.fromEntity(vehicle);
    }

    public List<VehicleResponse> getVehiclesByDriverId(String driverId) {
        return vehicleRepository.findByDriverId(driverId)
                .stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<VehicleResponse> getAllVehicles(VehicleType vehicleType, Boolean activeOnly) {
        List<Vehicle> list;
        if (vehicleType != null) {
            list = vehicleRepository.findByVehicleType(vehicleType);
        } else {
            list = vehicleRepository.findAll();
        }

        if (Boolean.TRUE.equals(activeOnly)) {
            list = list.stream().filter(v -> Boolean.TRUE.equals(v.getIsActive())).collect(Collectors.toList());
        }

        return list.stream().map(VehicleResponse::fromEntity).collect(Collectors.toList());
    }

    public VehicleResponse updateVehicle(String id, UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with ID: " + id));

        if (request.getLicensePlate() != null && !request.getLicensePlate().isBlank()) {
            String newPlate = request.getLicensePlate().toUpperCase().trim();
            if (!newPlate.equalsIgnoreCase(vehicle.getLicensePlate()) && vehicleRepository.existsByLicensePlateIgnoreCase(newPlate)) {
                throw new DuplicateResourceException("Vehicle with license plate '" + newPlate + "' is already registered");
            }
            vehicle.setLicensePlate(newPlate);
        }

        if (request.getMake() != null) vehicle.setMake(request.getMake());
        if (request.getModel() != null) vehicle.setModel(request.getModel());
        if (request.getYear() != null) vehicle.setYear(request.getYear());
        if (request.getColor() != null) vehicle.setColor(request.getColor());
        if (request.getVehicleType() != null) vehicle.setVehicleType(request.getVehicleType());
        if (request.getCapacity() != null) vehicle.setCapacity(request.getCapacity());
        if (request.getIsActive() != null) vehicle.setIsActive(request.getIsActive());

        vehicle.setUpdatedAt(LocalDateTime.now());
        return VehicleResponse.fromEntity(vehicleRepository.save(vehicle));
    }

    public void deleteVehicle(String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with ID: " + id));

        // If driver had this as active vehicle, unset it
        driverProfileRepository.findById(vehicle.getDriverId()).ifPresent(driver -> {
            if (id.equals(driver.getActiveVehicleId())) {
                driver.setActiveVehicleId(null);
                driver.setUpdatedAt(LocalDateTime.now());
                driverProfileRepository.save(driver);
            }
        });

        vehicleRepository.deleteById(id);
    }
}
