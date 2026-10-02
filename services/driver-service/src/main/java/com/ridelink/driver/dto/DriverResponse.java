package com.ridelink.driver.dto;

import com.ridelink.driver.model.Availability;
import com.ridelink.driver.model.DriverProfile;
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
public class DriverResponse {

    private String id;
    private String accountId;
    private String licenseNumber;
    private Availability availability;
    private String serviceArea;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double rating;
    private Integer totalRides;
    private String activeVehicleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DriverResponse fromEntity(DriverProfile profile) {
        if (profile == null) return null;
        return DriverResponse.builder()
                .id(profile.getId())
                .accountId(profile.getAccountId())
                .licenseNumber(profile.getLicenseNumber())
                .availability(profile.getAvailability())
                .serviceArea(profile.getServiceArea())
                .currentLatitude(profile.getCurrentLatitude())
                .currentLongitude(profile.getCurrentLongitude())
                .rating(profile.getRating())
                .totalRides(profile.getTotalRides())
                .activeVehicleId(profile.getActiveVehicleId())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
