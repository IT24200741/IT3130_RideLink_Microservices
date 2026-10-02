package com.ridelink.ride.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LocationDto {

    @NotBlank(message = "Location name cannot be blank")
    private String name;

    @NotNull(message = "Latitude is required")
    private Double lat;

    @NotNull(message = "Longitude is required")
    private Double lng;

    public LocationDto(){

    }

    public LocationDto(String name, Double lat, Double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    } 

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat){
        this.lat = lat;
    }

    public Double getLng(){
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    
}
