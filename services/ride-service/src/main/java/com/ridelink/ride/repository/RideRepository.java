package com.ridelink.ride.repository;

import com.ridelink.ride.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface RideRepository extends JpaRepository<Ride, Long> {

}
