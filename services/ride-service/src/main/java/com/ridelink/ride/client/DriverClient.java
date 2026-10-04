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
            DriverApiResponse response = restClient.post()
                    .uri("/api/v1/drivers/query-eligible")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<DriverApiResponse>() {});

            if (response != null && response.data() != null) {
                return response.data();
            }
            return Collections.emptyList();

        } catch (Exception ex) {
            throw new NoDriverAvailableException("Driver Service is currently unavailable or returned an error: " + ex.getMessage());
        }
    }
}