package com.ridelink.ride.model;

import jakarta.persistence.Embeddable;

@Embeddable 
public class Location {

    private String name;
    private Double lat;
    private Double lng;

    public Location() {

    }
    
    public Location(String name, Double lat, Double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
