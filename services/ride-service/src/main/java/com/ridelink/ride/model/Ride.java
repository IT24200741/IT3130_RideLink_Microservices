package com.ridelink.ride.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
public class Ride {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String passengerId;

    private String driverID;

    @Embedded 
    @AttributeOverrides({
        @AttributeOverride(name = "name",column = @Column(name = "pickup_name")),
        @AttributeOverride(name = "lat",column = @Column(name = "pickup_lat")),
        @AttributeOverride(name = "lng", column = @Column(name = "pickup_lng"))
    })
    private Location pickupLocation;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "destination_name")),
        @AttributeOverride(name = "lat", column = @Column(name = "destination_lat")),
        @AttributeOverride(name = "lng", column = @Column(name = "destination_lng"))
    })
    private Location destinationLocation;

    private String vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    
    private Double estimatedDistanceKm;
    private Double fare;
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

       @PrePersist
    protected void onCreate() {
        if (this.requestedAt == null) {
            this.requestedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = RideStatus.REQUESTED;
        }
    }

      public Ride() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassengerID() {
        return passengerId;
    }

    public void setPassengerID(String passengerID) {
        this.passengerId = passengerID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Location getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(Location destinationLocation) {
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

     public Double getFare() {
        return fare;
    }
    public void setFare(Double fare) {
        this.fare = fare;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

        public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

}