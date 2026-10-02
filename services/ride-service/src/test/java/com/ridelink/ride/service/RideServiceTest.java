package com.ridelink.ride.service;

import com.ridelink.ride.client.DriverClient;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.EligibleDriverResponse;
import com.ridelink.ride.dto.LocationDto;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.exception.InvalidRideStatusTransitionException;
import com.ridelink.ride.exception.NoDriverAvailableException;
import com.ridelink.ride.exception.RideNotFoundException;
import com.ridelink.ride.exception.UnauthorizedAccessException;
import com.ridelink.ride.model.Location;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverClient driverClient;

    @InjectMocks
    private RideService rideService;

    private Ride sampleRide;

    @BeforeEach
    void setUp() {
        sampleRide = new Ride();
        sampleRide.setId(1L);
        sampleRide.setPassengerID("pass-001");
        sampleRide.setVehicleType("CAR");
        sampleRide.setStatus(RideStatus.REQUESTED);
        sampleRide.setPickupLocation(new Location("Malabe", 6.9147, 79.9729));
        sampleRide.setDestinationLocation(new Location("Kaduwela", 6.9344, 79.9842));
        sampleRide.setEstimatedDistanceKm(2.5);
    }

    @Test
    @DisplayName("Happy Path: Should successfully create a ride and calculate distance")
    void createRide_ShouldSuccessfullyCreateRide() {
        CreateRideRequest request = new CreateRideRequest(
                "pass-001",
                new LocationDto("Malabe", 6.9147, 79.9729),
                new LocationDto("Kaduwela", 6.9344, 79.9842),
                "CAR"
        );

        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> {
            Ride r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        RideResponse response = rideService.createRide(request);

        assertNotNull(response);
        assertEquals("pass-001", response.getPassengerId());
        assertEquals(RideStatus.REQUESTED, response.getStatus());
        assertTrue(response.getEstimatedDistanceKm() > 0);
        verify(rideRepository, times(1)).save(any(Ride.class));
    }

    @Test
    @DisplayName("Happy Path: Should assign nearest eligible driver via DriverClient")
    void assignDriver_ShouldAssignDriver_WhenEligibleDriverAvailable() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));
        when(driverClient.queryEligibleDrivers(any())).thenReturn(List.of(
                new EligibleDriverResponse("drv_xyz789", "Sunil Silva", "+94719876543", 0.8)
        ));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        RideResponse response = rideService.assignDriver(1L);

        assertNotNull(response);
        assertEquals(RideStatus.ASSIGNED, response.getStatus());
        assertEquals("drv_xyz789", response.getDriverId());
        verify(driverClient, times(1)).queryEligibleDrivers(any());
    }

    @Test
    @DisplayName("Negative Scenario: Should throw NoDriverAvailableException when no drivers in radius")
    void assignDriver_ShouldThrowException_WhenNoDriversNearby() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));
        when(driverClient.queryEligibleDrivers(any())).thenReturn(Collections.emptyList());

        assertThrows(NoDriverAvailableException.class, () -> rideService.assignDriver(1L));
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    @DisplayName("Negative Scenario: Should throw InvalidRideStatusTransitionException on illegal jump")
    void updateRideStatus_ShouldThrowException_OnIllegalTransition() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));

        assertThrows(InvalidRideStatusTransitionException.class, () ->
                rideService.updateRideStatus(1L, RideStatus.COMPLETED, "driver-101", "DRIVER")
        );
    }

    @Test
    @DisplayName("Security: Should throw UnauthorizedAccessException when passenger views another's ride")
    void getRideById_ShouldThrowException_WhenUnauthorizedPassenger() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));

        assertThrows(UnauthorizedAccessException.class, () ->
                rideService.getRideById(1L, "pass-999", "PASSENGER")
        );
    }

    @Test
    @DisplayName("Negative Scenario: Should throw RideNotFoundException for non-existent ride ID")
    void getRideById_ShouldThrowException_WhenRideNotFound() {
        when(rideRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () ->
                rideService.getRideById(999L, "pass-001", "PASSENGER")
        );
    }
}