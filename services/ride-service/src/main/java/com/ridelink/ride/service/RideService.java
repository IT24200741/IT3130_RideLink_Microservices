package com.ridelink.ride.service;

import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.exception.InvalidRideStatusTransitionException;
import com.ridelink.ride.exception.RideNotFoundException;
import com.ridelink.ride.exception.UnauthorizedAccessException;
import com.ridelink.ride.model.Location;

import com.ridelink.ride.client.DriverClient;
import com.ridelink.ride.dto.EligibleDriverResponse;
import com.ridelink.ride.dto.QueryEligibleDriversRequest;
import com.ridelink.ride.exception.NoDriverAvailableException;

import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service 
public class RideService {
    
    private final RideRepository rideRepository;
    private final DriverClient driverClient;

    @Autowired 
    public RideService(RideRepository rideRepository, DriverClient driverClient) {
        this.rideRepository = rideRepository;
        this.driverClient = driverClient;
    }

     @Transactional
    public RideResponse createRide(CreateRideRequest request) {
        Ride ride = new Ride();
        ride.setPassengerID(request.getPassengerId());
        ride.setVehicleType(request.getVehicleType());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedAt(LocalDateTime.now());

         Location pickup = new Location(
            request.getPickupLocation().getName(),
            request.getPickupLocation().getLat(),
            request.getPickupLocation().getLng()
        );
        ride.setPickupLocation(pickup);

          Location destination = new Location(
            request.getDestinationLocation().getName(),
            request.getDestinationLocation().getLat(),
            request.getDestinationLocation().getLng()
        );
        ride.setDestinationLocation(destination);

         double distance = calculateDistanceKm(pickup.getLat(), pickup.getLng(), destination.getLat(), destination.getLng());
        ride.setEstimatedDistanceKm(Math.round(distance * 10.0) / 10.0);
        Ride savedRide = rideRepository.save(ride);
        return RideResponse.fromEntity(savedRide);
    }

     public List<RideResponse> getRides(String callerId, String callerRole) {
        List<Ride> rides;
        if ("ADMIN".equalsIgnoreCase(callerRole)) {
            rides = rideRepository.findAll();
        } else if ("DRIVER".equalsIgnoreCase(callerRole) && callerId != null) {
            rides = rideRepository.findByDriverID(callerId);
        } else if (callerId != null && !callerId.isBlank()) {
            rides = rideRepository.findByPassengerId(callerId);
        } else {
            rides = rideRepository.findAll();
        }
        return rides.stream().map(RideResponse::fromEntity).collect(Collectors.toList());
    }
    public List<RideResponse> getAllRides() {
        return getRides(null, "ADMIN");
    }
       public RideResponse getRideById(Long id, String callerId, String callerRole) {
        Objects.requireNonNull(id, "Ride ID must not be null");
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(id));

            if (callerId != null && !callerId.isBlank() && !"ADMIN".equalsIgnoreCase(callerRole)) {
            boolean isPassenger = callerId.equals(ride.getPassengerID());
            boolean isDriver = callerId.equals(ride.getDriverID());
            if (!isPassenger && !isDriver) {
                throw new UnauthorizedAccessException("You are not authorized to view this trip record.");
            }
        }
        return RideResponse.fromEntity(ride);
    }
    public RideResponse getRideById(Long id) {
         return getRideById(id, null, "ADMIN");
    }

        @Transactional
    public RideResponse assignDriver(Long rideId) {
        Objects.requireNonNull(rideId, "Ride ID must not be null");
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new InvalidRideStatusTransitionException(
                "Cannot assign driver to ride in status: " + ride.getStatus()
            );
        }

        QueryEligibleDriversRequest query = new QueryEligibleDriversRequest(
            ride.getPickupLocation().getLat(),
            ride.getPickupLocation().getLng(),
            ride.getVehicleType(),
            5.0
        );

        List<EligibleDriverResponse> eligibleDrivers = driverClient.queryEligibleDrivers(query);

        if (eligibleDrivers == null || eligibleDrivers.isEmpty()) {
            throw new NoDriverAvailableException("No eligible drivers available within 5km radius");
        }

        EligibleDriverResponse selectedDriver = eligibleDrivers.get(0);
        ride.setDriverID(selectedDriver.driverId());
        ride.setStatus(RideStatus.ASSIGNED);

        Ride updatedRide = rideRepository.save(ride);
        return RideResponse.fromEntity(updatedRide);
    }
   

     @Transactional
    public RideResponse updateRideStatus(Long id, RideStatus newStatus, String callerId, String callerRole) {
        Objects.requireNonNull(id, "Ride ID must not be null");
        Objects.requireNonNull(newStatus, "New status must not be null");
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(id));

                RideStatus currentStatus = ride.getStatus();
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new InvalidRideStatusTransitionException(
                "Invalid status transition: Cannot transition ride from " + currentStatus + " to " + newStatus
            );
        }

         if (callerId != null && !callerId.isBlank() && !"ADMIN".equalsIgnoreCase(callerRole)) {
            // Passenger can ONLY cancel
            if ("PASSENGER".equalsIgnoreCase(callerRole)) {
                if (newStatus != RideStatus.CANCELLED) {
                    throw new UnauthorizedAccessException("Passengers are not permitted to transition rides to " + newStatus);
                }
                if (!callerId.equals(ride.getPassengerID())) {
                    throw new UnauthorizedAccessException("You can only cancel your own rides.");
                }
            }

            if ("DRIVER".equalsIgnoreCase(callerRole)) {
                if (ride.getDriverID() != null && !callerId.equals(ride.getDriverID())) {
                    throw new UnauthorizedAccessException("Only the assigned driver can update this ride's status.");
                }
            }
        }
        ride.setStatus(newStatus);
        if (newStatus == RideStatus.COMPLETED) {
            ride.setCompletedAt(LocalDateTime.now());
        }
        Ride updatedRide = rideRepository.save(ride);
        return RideResponse.fromEntity(updatedRide);
    }
    public RideResponse updateRideStatus(Long id, RideStatus newStatus) {
        return updateRideStatus(id, newStatus, null, "ADMIN");
    }



      private boolean isValidTransition(RideStatus from, RideStatus to) {
        if (from == null || to == null) {
            return false;
        }
        return switch (from) {
            case REQUESTED -> to == RideStatus.ASSIGNED || to == RideStatus.CANCELLED;
            case ASSIGNED -> to == RideStatus.ACCEPTED || to == RideStatus.CANCELLED;
            case ACCEPTED -> to == RideStatus.IN_PROGRESS || to == RideStatus.CANCELLED;
            case IN_PROGRESS -> to == RideStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
                 };
    }

        private double calculateDistanceKm(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return 3.5; 
        }
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.max(1.0, R * c);
    }
}

  