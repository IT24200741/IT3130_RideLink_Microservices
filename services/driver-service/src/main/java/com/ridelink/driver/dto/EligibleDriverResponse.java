package com.ridelink.driver.dto;

import com.ridelink.driver.model.Availability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibleDriverResponse {

    private String driverId;
    private String accountId;
    private String licenseNumber;
    private Availability availability;
    private String serviceArea;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double rating;
    private Integer totalRides;
    private Double distanceKm;
    private VehicleResponse vehicle;
}
