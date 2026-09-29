package com.ridelink.driver.dto;

import com.ridelink.driver.model.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateVehicleRequest {

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotBlank(message = "Vehicle make is required (e.g. Toyota)")
    private String make;

    @NotBlank(message = "Vehicle model is required (e.g. Prius)")
    private String model;

    @NotNull(message = "Manufacturing year is required")
    @Min(value = 1990, message = "Year must be 1990 or later")
    private Integer year;

    @NotBlank(message = "Vehicle color is required")
    private String color;

    @NotBlank(message = "License plate number is required")
    private String licensePlate;

    @NotNull(message = "Vehicle type is required (CAR, MOTORCYCLE, THREE_WHEELER, VAN, LUXURY)")
    private VehicleType vehicleType;

    @NotNull(message = "Passenger capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}
