package com.ridelink.ride.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
