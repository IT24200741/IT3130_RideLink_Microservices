package com.ridelink.ride.exception;

public class InvalidRideStatusTransitionException extends RuntimeException {

    public InvalidRideStatusTransitionException(String message) {
        super(message);
    }
}