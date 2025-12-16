package com.Chese.KACM_Recommendation.model;

import java.util.List;

/**
 * Represents a complete route path with metadata
 */
public class RoutePath {
    private List<LocationCoordinate> path;
    private RouteCriteria criteria;
    private String description; // e.g., "Fastest", "Most Scenic", "Avoid Highways"
    private double totalDistance; // in km
    private double totalTime; // in minutes
    
    public RoutePath(List<LocationCoordinate> path, RouteCriteria criteria, String description) {
        this.path = path;
        this.criteria = criteria;
        this.description = description;
    }
    
    // Getters and setters
    public List<LocationCoordinate> getPath() { return path; }
    public void setPath(List<LocationCoordinate> path) { this.path = path; }
    public RouteCriteria getCriteria() { return criteria; }
    public void setCriteria(RouteCriteria criteria) { this.criteria = criteria; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }
    public double getTotalTime() { return totalTime; }
    public void setTotalTime(double totalTime) { this.totalTime = totalTime; }
}

