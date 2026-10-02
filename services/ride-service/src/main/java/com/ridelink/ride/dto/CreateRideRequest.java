package com.ridelink.ride.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateRideRequest {

    @NotBlank(message = "Passenger ID is required")
    private String passengerId;

    @NotNull(message = "Pickup location is required")
    @Valid 
    private LocationDto pickupLocation;

    @NotNull(message = "Destination location is required")
    @Valid 
    private LocationDto destinationLocation;

    @NotBlank(message ="Vehicle type is required (e.g.,CAR,BIKE,TUK)")
    private String vehicleType;

    public CreateRideRequest() {

    }

    public CreateRideRequest(String passengerId, LocationDto pickupLocation, LocationDto destinationLocation, String vehicleType){
        this.passengerId = passengerId;
        this.pickupLocation = pickupLocation;
        this.destinationLocation = destinationLocation;
        this.vehicleType = vehicleType;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public LocationDto getPickupLocation(){
        return pickupLocation;
    }

    public void setPickupLocation(LocationDto pickupLocation) {
        this.pickupLocation = pickupLocation;
    }
    public LocationDto getDestinationLocation(){
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

    
}
