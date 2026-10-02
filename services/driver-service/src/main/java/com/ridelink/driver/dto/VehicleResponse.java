package com.ridelink.driver.dto;

import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {

    private String id;
    private String driverId;
    private String make;
    private String model;
    private Integer year;
    private String color;
    private String licensePlate;
    private VehicleType vehicleType;
    private Integer capacity;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static VehicleResponse fromEntity(Vehicle vehicle) {
        if (vehicle == null) return null;
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .driverId(vehicle.getDriverId())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .color(vehicle.getColor())
                .licensePlate(vehicle.getLicensePlate())
                .vehicleType(vehicle.getVehicleType())
                .capacity(vehicle.getCapacity())
                .isActive(vehicle.getIsActive())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
