package com.Chese.KACM_Recommendation.model;

import java.util.Map;

/**
 * Represents an edge with time-dependent weights (traffic-aware)
 * Weight function: w_e(t) = length / speed_e(t)
 */
public class TimeDependentEdge {
    private String from;
    private String to;
    private double length; // in km
    private Map<Integer, Double> speedProfile; // hour -> speed (km/h)
    private double baseSpeed; // default speed when no profile
    
    public TimeDependentEdge(String from, String to, double length, double baseSpeed) {
        this.from = from;
        this.to = to;
        this.length = length;
        this.baseSpeed = baseSpeed;
        this.speedProfile = new java.util.HashMap<>();
    }
    
    /**
     * Get travel time at a specific departure time
     * @param departureHour hour of day (0-23)
     * @return travel time in minutes
     */
    public double getTravelTime(int departureHour) {
        double speed = speedProfile.getOrDefault(departureHour, baseSpeed);
        // Apply traffic congestion: reduce speed during rush hours
        if (departureHour >= 7 && departureHour <= 9) {
            speed *= 0.6; // 40% slower during morning rush
        } else if (departureHour >= 17 && departureHour <= 19) {
            speed *= 0.65; // 35% slower during evening rush
        }
        return (length / speed) * 60; // convert to minutes
    }
    
    /**
     * Set speed profile for specific hours
     */
    public void setSpeedProfile(int hour, double speed) {
        speedProfile.put(hour, speed);
    }
    
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getLength() { return length; }
    public double getBaseSpeed() { return baseSpeed; }
}

