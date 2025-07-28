package com.flowclaims.backend.model;

public class HeatMapPoint {
    private double latitude;
    private double longitude;
    private double weight;

    public HeatMapPoint(double latitude, double longitude, double weight) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.weight = weight;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getWeight() {
        return weight;
    }

    public void addWeight(double additional) {
        this.weight += additional;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
