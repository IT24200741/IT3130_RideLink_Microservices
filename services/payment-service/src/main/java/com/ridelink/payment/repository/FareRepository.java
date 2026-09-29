package com.ridelink.payment.repository;

import com.ridelink.payment.model.Fare;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FareRepository extends MongoRepository<Fare, String> {

    Optional<Fare> findByRideId(String rideId);
}