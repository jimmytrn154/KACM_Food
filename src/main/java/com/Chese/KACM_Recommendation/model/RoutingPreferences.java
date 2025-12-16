package com.Chese.KACM_Recommendation.model;

/**
 * User preferences for routing
 */
public class RoutingPreferences {
    private double timeWeight = 1.0;
    private double highwayWeight = 0.5;  // penalty weight for highways
    private double scenicWeight = 0.3;   // preference for scenic routes
    private double safetyWeight = 0.4;   // preference for safe routes
    private double turnWeight = 0.2;     // penalty for turns
    private double tollWeight = 0.1;     // penalty for tolls
    
    public RoutingPreferences() {}
    
    public RoutingPreferences(double timeWeight, double highwayWeight, double scenicWeight,
                             double safetyWeight, double turnWeight, double tollWeight) {
        this.timeWeight = timeWeight;
        this.highwayWeight = highwayWeight;
        this.scenicWeight = scenicWeight;
        this.safetyWeight = safetyWeight;
        this.turnWeight = turnWeight;
        this.tollWeight = tollWeight;
    }
    
    // Getters and setters
    public double getTimeWeight() { return timeWeight; }
    public void setTimeWeight(double timeWeight) { this.timeWeight = timeWeight; }
    public double getHighwayWeight() { return highwayWeight; }
    public void setHighwayWeight(double highwayWeight) { this.highwayWeight = highwayWeight; }
    public double getScenicWeight() { return scenicWeight; }
    public void setScenicWeight(double scenicWeight) { this.scenicWeight = scenicWeight; }
    public double getSafetyWeight() { return safetyWeight; }
    public void setSafetyWeight(double safetyWeight) { this.safetyWeight = safetyWeight; }
    public double getTurnWeight() { return turnWeight; }
    public void setTurnWeight(double turnWeight) { this.turnWeight = turnWeight; }
    public double getTollWeight() { return tollWeight; }
    public void setTollWeight(double tollWeight) { this.tollWeight = tollWeight; }
}

