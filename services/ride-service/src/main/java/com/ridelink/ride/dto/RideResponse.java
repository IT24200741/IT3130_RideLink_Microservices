package com.ridelink.ride.dto;

import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;

import java.time.LocalDateTime;

public class RideResponse {

    private Long rideId;
    private String passengerId;
    private String driverId;
    private LocationDto pickupLocation;
    private LocationDto destinationLocation;
    private String vehicleType;
    private RideStatus status;
    private Double estimatedDistanceKm;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    
    public RideResponse() {
    }

    
    public RideResponse(Long rideId, String passengerId, String driverId, LocationDto pickupLocation,
                        LocationDto destinationLocation, String vehicleType, RideStatus status,
                        Double estimatedDistanceKm, LocalDateTime createdAt, LocalDateTime completedAt) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.pickupLocation = pickupLocation;
        this.destinationLocation = destinationLocation;
        this.vehicleType = vehicleType;
        this.status = status;
        this.estimatedDistanceKm = estimatedDistanceKm;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public static RideResponse fromEntity(Ride ride) {
        if (ride == null) {
            return null;
        }

        LocationDto pickup = null;
        if (ride.getPickupLocation() != null) {
            pickup = new LocationDto(
                ride.getPickupLocation().getName(),
                ride.getPickupLocation().getLat(),
                ride.getPickupLocation().getLng()
            );
        }

        LocationDto destination = null;
        if (ride.getDestinationLocation() != null) {
            destination = new LocationDto(
                ride.getDestinationLocation().getName(),
                ride.getDestinationLocation().getLat(),
                ride.getDestinationLocation().getLng()
            );
        }

        return new RideResponse(
            ride.getId(),
            ride.getPassengerID(),
            ride.getDriverID(),
            pickup,
            destination,
            ride.getVehicleType(),
            ride.getStatus(),
            ride.getEstimatedDistanceKm(),
            ride.getRequestedAt(),
            ride.getCompletedAt()
        );
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public LocationDto getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(LocationDto pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public LocationDto getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(LocationDto destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public Double getEstimatedDistanceKm() {
        return estimatedDistanceKm;
    }

    public void setEstimatedDistanceKm(Double estimatedDistanceKm) {
        this.estimatedDistanceKm = estimatedDistanceKm;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}