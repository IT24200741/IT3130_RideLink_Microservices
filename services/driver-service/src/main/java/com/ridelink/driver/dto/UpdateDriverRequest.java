package com.ridelink.driver.dto;

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
public class UpdateDriverRequest {

    private String licenseNumber;
    private String serviceArea;
    private String activeVehicleId;
}
