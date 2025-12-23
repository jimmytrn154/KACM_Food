package com.Chese.KACM_Recommendation.model;

/**
 * Multi-criteria routing weights
 */
public class RouteCriteria {
    private double travelTime;      // in minutes
    private double highwayPenalty;  // penalty for using highways (0-1)
    private double scenicScore;    // preference for scenic routes (0-1)
    private double safetyScore;     // safety/lighting score (0-1)
    private double turnPenalty;     // penalty for many turns (0-1)
    private double tollCost;        // toll cost in currency units
    
    public RouteCriteria() {
        this.travelTime = 0;
        this.highwayPenalty = 0;
        this.scenicScore = 0;
        this.safetyScore = 0;
        this.turnPenalty = 0;
        this.tollCost = 0;
    }
    
    public RouteCriteria(double travelTime, double highwayPenalty, double scenicScore, 
                        double safetyScore, double turnPenalty, double tollCost) {
        this.travelTime = travelTime;
        this.highwayPenalty = highwayPenalty;
        this.scenicScore = scenicScore;
        this.safetyScore = safetyScore;
        this.turnPenalty = turnPenalty;
        this.tollCost = tollCost;
    }
    
    /**
     * Combine criteria into a single weighted cost
     * When scenicWeight is high, routes become longer but more scenic
     */
    public double getWeightedCost(RoutingPreferences prefs) {
        double baseCost = travelTime * prefs.getTimeWeight()
             + highwayPenalty * prefs.getHighwayWeight()
             - safetyScore * prefs.getSafetyWeight()
             + turnPenalty * prefs.getTurnWeight()
             + tollCost * prefs.getTollWeight();
        
        // Scenic routes: higher scenicWeight makes scenic edges much cheaper
        // This encourages longer paths through scenic areas
        double scenicBonus = scenicScore * prefs.getScenicWeight();
        
        // When scenicWeight is high (>1.0), heavily penalize highways and reward scenic paths
        if (prefs.getScenicWeight() > 1.0) {
            // Make highways very expensive (forces longer scenic routes)
            double highwayMultiplier = 1.0 + (prefs.getScenicWeight() - 1.0) * 2.0;
            baseCost += highwayPenalty * prefs.getHighwayWeight() * (highwayMultiplier - 1.0);
            
            // Make scenic paths much cheaper (encourages longer scenic routes)
            scenicBonus *= 2.0; // Double the scenic bonus when preference is high
        }
        
        return baseCost - scenicBonus; // Subtract scenic bonus (negative = lower cost)
    }
    
    public RouteCriteria add(RouteCriteria other) {
        return new RouteCriteria(
            this.travelTime + other.travelTime,
            this.highwayPenalty + other.highwayPenalty,
            this.scenicScore + other.scenicScore,
            this.safetyScore + other.safetyScore,
            this.turnPenalty + other.turnPenalty,
            this.tollCost + other.tollCost
        );
    }
    
    // Getters and setters
    public double getTravelTime() { return travelTime; }
    public void setTravelTime(double travelTime) { this.travelTime = travelTime; }
    public double getHighwayPenalty() { return highwayPenalty; }
    public void setHighwayPenalty(double highwayPenalty) { this.highwayPenalty = highwayPenalty; }
    public double getScenicScore() { return scenicScore; }
    public void setScenicScore(double scenicScore) { this.scenicScore = scenicScore; }
    public double getSafetyScore() { return safetyScore; }
    public void setSafetyScore(double safetyScore) { this.safetyScore = safetyScore; }
    public double getTurnPenalty() { return turnPenalty; }
    public void setTurnPenalty(double turnPenalty) { this.turnPenalty = turnPenalty; }
    public double getTollCost() { return tollCost; }
    public void setTollCost(double tollCost) { this.tollCost = tollCost; }
}

