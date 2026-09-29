package com.ridelink.driver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "driver_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfile {

    @Id
    private String id;

    private String accountId;
    private String licenseNumber;
    private Availability availability;
    private String serviceArea;
    private Double currentLatitude;
    private Double currentLongitude;

    @Builder.Default
    private Double rating = 5.0;

    @Builder.Default
    private Integer totalRides = 0;

    private String activeVehicleId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}