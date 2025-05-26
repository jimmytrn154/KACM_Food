package com.Chese.KACM_Recommendation.model;

public class LocationCoordinate {
    private String name;
    private double lat;
    private double lon;

    public LocationCoordinate() {}
    public LocationCoordinate(String name, double lat, double lon) {
        this.name = name;
        this.lat   = lat;
        this.lon   = lon;
    }
    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }
}