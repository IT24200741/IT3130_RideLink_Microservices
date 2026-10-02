package com.ridelink.ride.dto;


import com.ridelink.ride.model.RideStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

    
    @NotNull(message = "Ride status is required")
    private RideStatus status;
    public UpdateStatusRequest() {
    }
    public UpdateStatusRequest(RideStatus status) {
        this.status = status;
    }
    public RideStatus getStatus() {
        return status;
    }
    public void setStatus(RideStatus status) {
        this.status = status;
    }
}
    