package com.ridelink.driver.repository;

import com.ridelink.driver.model.Availability;
import com.ridelink.driver.model.DriverProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DriverProfileRepository extends MongoRepository<DriverProfile, String> {

    Optional<DriverProfile> findByAccountId(String accountId);

    boolean existsByAccountId(String accountId);

    boolean existsByLicenseNumber(String licenseNumber);

    List<DriverProfile> findByAvailability(Availability availability);

    List<DriverProfile> findByServiceAreaIgnoreCase(String serviceArea);

    List<DriverProfile> findByAvailabilityAndServiceAreaIgnoreCase(Availability availability, String serviceArea);
}