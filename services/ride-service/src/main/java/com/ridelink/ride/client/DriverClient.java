package com.ridelink.ride.client;

import com.ridelink.ride.dto.EligibleDriverResponse;
import com.ridelink.ride.dto.QueryEligibleDriversRequest;
import com.ridelink.ride.exception.NoDriverAvailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Component
public class DriverClient {

    private final RestClient restClient;

    public DriverClient(@Value("${services.driver.url:http://localhost:8082}") String driverServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(driverServiceUrl)
                .build();
    }

    public record DriverApiResponse(
            boolean success,
            Integer count,
            List<EligibleDriverResponse> data
    ) {}

    public List<EligibleDriverResponse> queryEligibleDrivers(QueryEligibleDriversRequest request) {
        try {
            // Randi's actual endpoint: GET /api/drivers/eligible
            List<EligibleDriverResponse> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/drivers/eligible")
                            .queryParam("latitude", request.pickupLat())
                            .queryParam("longitude", request.pickupLng())
                            .queryParam("radiusKm", request.maxRadiusKm())
                            .queryParam("vehicleType", request.vehicleType())
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<EligibleDriverResponse>>() {});

            if (response != null) {
                return response;
            }
            return Collections.emptyList();

        } catch (Exception ex) {
            throw new NoDriverAvailableException("Driver Service is currently unavailable or returned an error: " + ex.getMessage());
        }
    }
}