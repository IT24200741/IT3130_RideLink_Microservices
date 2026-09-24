package com.ridelink.ride.service;

import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.exception.InvalidRideStatusTransitionException;
import com.ridelink.ride.exception.RideNotFoundException;
import com.ridelink.ride.model.Location;
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

    @Autowired 
    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
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

      public List<RideResponse> getAllRides() {
        return rideRepository.findAll()
                .stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }

       public RideResponse getRideById(Long id) {
        Objects.requireNonNull(id, "Ride ID must not be null");
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(id));
        return RideResponse.fromEntity(ride);
    }

      @Transactional
    public RideResponse updateRideStatus(Long id, RideStatus newStatus) {
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
        ride.setStatus(newStatus);

         if (newStatus == RideStatus.COMPLETED) {
            ride.setCompletedAt(LocalDateTime.now());
        }
        Ride updatedRide = rideRepository.save(ride);
        return RideResponse.fromEntity(updatedRide);
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

  