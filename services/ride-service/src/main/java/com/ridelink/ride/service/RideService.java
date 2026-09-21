package com.ridelink.ride.service;

import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service 
public class RideService {
    
    private final RideRepository rideRepository;

    @Autowired 
    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public Ride createRide(Ride ride) {
        if (ride.getStatus() ==  null) {
            ride.setStatus(RideStatus.REQUESTED);
        }
        return rideRepository.save(ride);
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Optional<Ride> getRideById(Long id) {
        return rideRepository.findById(id);
    }

    public Ride updateRideStatus(Long id, RideStatus status) {
        Ride ride = rideRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Ride not found with id: " + id));
        ride.setStatus(status);
        return rideRepository.save(ride);
    }

}

