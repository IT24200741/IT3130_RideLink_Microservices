package com.ridelink.driver.repository;

import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.model.VehicleType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {

    List<Vehicle> findByDriverId(String driverId);

    Optional<Vehicle> findByLicensePlateIgnoreCase(String licensePlate);

    boolean existsByLicensePlateIgnoreCase(String licensePlate);

    List<Vehicle> findByVehicleType(VehicleType vehicleType);

    List<Vehicle> findByDriverIdAndIsActiveTrue(String driverId);
}
