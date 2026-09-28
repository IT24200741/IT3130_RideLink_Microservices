package com.ridelink.driver.controller;

import com.ridelink.driver.model.DriverProfile;
import com.ridelink.driver.service.DriverProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
public class DriverProfileController {

    private final DriverProfileService service;

    public DriverProfileController(DriverProfileService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverProfile create(
            @RequestBody DriverProfile profile) {
        return service.create(profile);
    }
}