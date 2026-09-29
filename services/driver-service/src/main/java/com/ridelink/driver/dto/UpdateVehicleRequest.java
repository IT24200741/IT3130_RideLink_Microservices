package com.ridelink.driver.dto;

import com.ridelink.driver.model.VehicleType;
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
public class UpdateVehicleRequest {

    private String make;
    private String model;
    private Integer year;
    private String color;
    private String licensePlate;
    private VehicleType vehicleType;
    private Integer capacity;
    private Boolean isActive;
}
