package com.ridelink.payment.exception;

public class FareNotFoundException extends RuntimeException {
    public FareNotFoundException(String message) {
        super(message);
    }
}
