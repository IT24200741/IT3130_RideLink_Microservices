package com.ridelink.payment.repository;

import com.ridelink.payment.model.Payment;
import com.ridelink.payment.model.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByRideId(String rideId);

    List<Payment> findByPassengerId(String passengerId);

    List<Payment> findByDriverId(String driverId);

    List<Payment> findByStatus(PaymentStatus status);
}