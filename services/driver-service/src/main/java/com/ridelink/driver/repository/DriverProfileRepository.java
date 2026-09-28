package com.ridelink.driver.repository;

import com.ridelink.driver.model.DriverProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DriverProfileRepository
        extends MongoRepository<DriverProfile, String> {
}