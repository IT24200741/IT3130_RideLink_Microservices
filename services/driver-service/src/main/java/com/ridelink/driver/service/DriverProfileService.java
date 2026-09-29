package com.ridelink.driver.service;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.exception.DriverNotFoundException;
import com.ridelink.driver.exception.DuplicateResourceException;
import com.ridelink.driver.model.Availability;
import com.ridelink.driver.model.DriverProfile;
import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.model.VehicleType;
import com.ridelink.driver.repository.DriverProfileRepository;
import com.ridelink.driver.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverProfileService {

    private final DriverProfileRepository repository;
    private final VehicleRepository vehicleRepository;

    public DriverProfileService(DriverProfileRepository repository, VehicleRepository vehicleRepository) {
        this.repository = repository;
        this.vehicleRepository = vehicleRepository;
    }

    public DriverResponse create(CreateDriverRequest request) {
        if (repository.existsByAccountId(request.getAccountId())) {
            throw new DuplicateResourceException("Driver profile already exists for account ID: " + request.getAccountId());
        }

        if (repository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("Driver profile with license number '" + request.getLicenseNumber() + "' already exists");
        }

        DriverProfile profile = DriverProfile.builder()
                .accountId(request.getAccountId())
                .licenseNumber(request.getLicenseNumber().toUpperCase().trim())
                .availability(Availability.UNAVAILABLE)
                .serviceArea(request.getServiceArea().trim())
                .currentLatitude(request.getCurrentLatitude())
                .currentLongitude(request.getCurrentLongitude())
                .rating(5.0)
                .totalRides(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        DriverProfile saved = repository.save(profile);
        return DriverResponse.fromEntity(saved);
    }

    public DriverResponse getById(String id) {
        DriverProfile profile = repository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver profile not found with ID: " + id));
        return DriverResponse.fromEntity(profile);
    }

    public DriverResponse getByAccountId(String accountId) {
        DriverProfile profile = repository.findByAccountId(accountId)
                .orElseThrow(() -> new DriverNotFoundException("Driver profile not found for account ID: " + accountId));
        return DriverResponse.fromEntity(profile);
    }

    public List<DriverResponse> getAll(Availability availability, String serviceArea) {
        List<DriverProfile> profiles;

        if (availability != null && serviceArea != null && !serviceArea.isBlank()) {
            profiles = repository.findByAvailabilityAndServiceAreaIgnoreCase(availability, serviceArea.trim());
        } else if (availability != null) {
            profiles = repository.findByAvailability(availability);
        } else if (serviceArea != null && !serviceArea.isBlank()) {
            profiles = repository.findByServiceAreaIgnoreCase(serviceArea.trim());
        } else {
            profiles = repository.findAll();
        }

        return profiles.stream().map(DriverResponse::fromEntity).collect(Collectors.toList());
    }

    public DriverResponse update(String id, UpdateDriverRequest request) {
        DriverProfile profile = repository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver profile not found with ID: " + id));

        if (request.getLicenseNumber() != null && !request.getLicenseNumber().isBlank()) {
            String newLicense = request.getLicenseNumber().toUpperCase().trim();
            if (!newLicense.equalsIgnoreCase(profile.getLicenseNumber()) && repository.existsByLicenseNumber(newLicense)) {
                throw new DuplicateResourceException("Driver profile with license number '" + newLicense + "' already exists");
            }
            profile.setLicenseNumber(newLicense);
        }

        if (request.getServiceArea() != null && !request.getServiceArea().isBlank()) {
            profile.setServiceArea(request.getServiceArea().trim());
        }

        if (request.getActiveVehicleId() != null) {
            if (!request.getActiveVehicleId().isBlank()) {
                // Verify vehicle belongs to driver
                Vehicle v = vehicleRepository.findById(request.getActiveVehicleId())
                        .orElseThrow(() -> new DriverNotFoundException("Vehicle not found with ID: " + request.getActiveVehicleId()));
                if (!id.equals(v.getDriverId())) {
                    throw new IllegalArgumentException("Vehicle " + request.getActiveVehicleId() + " does not belong to driver " + id);
                }
            }
            profile.setActiveVehicleId(request.getActiveVehicleId().isBlank() ? null : request.getActiveVehicleId());
        }

        profile.setUpdatedAt(LocalDateTime.now());
        return DriverResponse.fromEntity(repository.save(profile));
    }

    public DriverResponse updateAvailability(String id, UpdateAvailabilityRequest request) {
        DriverProfile profile = repository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver profile not found with ID: " + id));

        profile.setAvailability(request.getAvailability());
        profile.setUpdatedAt(LocalDateTime.now());
        return DriverResponse.fromEntity(repository.save(profile));
    }

    public DriverResponse updateLocation(String id, UpdateLocationRequest request) {
        DriverProfile profile = repository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver profile not found with ID: " + id));

        profile.setCurrentLatitude(request.getLatitude());
        profile.setCurrentLongitude(request.getLongitude());
        profile.setUpdatedAt(LocalDateTime.now());
        return DriverResponse.fromEntity(repository.save(profile));
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new DriverNotFoundException("Driver profile not found with ID: " + id);
        }
        // Also delete associated vehicles
        List<Vehicle> vehicles = vehicleRepository.findByDriverId(id);
        vehicleRepository.deleteAll(vehicles);
        repository.deleteById(id);
    }

    /**
     * Find eligible / nearby available drivers matching dispatch criteria.
     * Uses the Haversine formula to compute great-circle distance in kilometers.
     */
    public List<EligibleDriverResponse> findEligibleDrivers(
            Double pickupLat,
            Double pickupLng,
            Double maxRadiusKm,
            VehicleType vehicleType,
            String serviceArea
    ) {
        // 1. Only AVAILABLE drivers
        List<DriverProfile> availableDrivers = repository.findByAvailability(Availability.AVAILABLE);

        if (serviceArea != null && !serviceArea.isBlank()) {
            availableDrivers = availableDrivers.stream()
                    .filter(d -> d.getServiceArea() != null && d.getServiceArea().equalsIgnoreCase(serviceArea.trim()))
                    .collect(Collectors.toList());
        }

        double radius = (maxRadiusKm != null && maxRadiusKm > 0) ? maxRadiusKm : 10.0;

        List<EligibleDriverResponse> results = new ArrayList<>();

        for (DriverProfile driver : availableDrivers) {
            Double distanceKm = null;

            // Compute distance if coordinates are present
            if (pickupLat != null && pickupLng != null && driver.getCurrentLatitude() != null && driver.getCurrentLongitude() != null) {
                distanceKm = calculateDistanceKm(pickupLat, pickupLng, driver.getCurrentLatitude(), driver.getCurrentLongitude());
                if (distanceKm > radius) {
                    continue; // Out of radius
                }
            }

            // Get active vehicle info
            VehicleResponse vehicleResponse = null;
            if (driver.getActiveVehicleId() != null) {
                vehicleResponse = vehicleRepository.findById(driver.getActiveVehicleId())
                        .map(VehicleResponse::fromEntity)
                        .orElse(null);
            }

            // Filter by vehicle type if requested
            if (vehicleType != null) {
                if (vehicleResponse == null || vehicleResponse.getVehicleType() != vehicleType) {
                    continue; // Doesn't match requested vehicle type
                }
            }

            EligibleDriverResponse response = EligibleDriverResponse.builder()
                    .driverId(driver.getId())
                    .accountId(driver.getAccountId())
                    .licenseNumber(driver.getLicenseNumber())
                    .availability(driver.getAvailability())
                    .serviceArea(driver.getServiceArea())
                    .currentLatitude(driver.getCurrentLatitude())
                    .currentLongitude(driver.getCurrentLongitude())
                    .rating(driver.getRating())
                    .totalRides(driver.getTotalRides())
                    .distanceKm(distanceKm != null ? Math.round(distanceKm * 100.0) / 100.0 : null)
                    .vehicle(vehicleResponse)
                    .build();

            results.add(response);
        }

        // Sort by closest distance first
        results.sort(Comparator.comparing(
                EligibleDriverResponse::getDistanceKm,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        return results;
    }

    /**
     * Haversine formula to calculate the distance between two GPS coordinates in kilometers.
     */
    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}