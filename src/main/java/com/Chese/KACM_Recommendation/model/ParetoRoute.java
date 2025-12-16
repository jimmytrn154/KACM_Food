package com.Chese.KACM_Recommendation.model;

/**
 * Represents a Pareto-optimal route (non-dominated solution)
 */
public class ParetoRoute {
    private RoutePath route;
    private boolean dominated; // true if this route is dominated by another
    
    public ParetoRoute(RoutePath route) {
        this.route = route;
        this.dominated = false;
    }
    
    /**
     * Check if this route dominates another
     * Route A dominates Route B if A is better in at least one criterion and not worse in any
     */
    public boolean dominates(ParetoRoute other) {
        RouteCriteria thisC = this.route.getCriteria();
        RouteCriteria otherC = other.route.getCriteria();
        
        boolean betterInAtLeastOne = false;
        
        // Lower is better for all criteria
        if (thisC.getTravelTime() < otherC.getTravelTime()) betterInAtLeastOne = true;
        if (thisC.getHighwayPenalty() < otherC.getHighwayPenalty()) betterInAtLeastOne = true;
        if (thisC.getTurnPenalty() < otherC.getTurnPenalty()) betterInAtLeastOne = true;
        if (thisC.getTollCost() < otherC.getTollCost()) betterInAtLeastOne = true;
        
        // Higher is better for these
        if (thisC.getScenicScore() > otherC.getScenicScore()) betterInAtLeastOne = true;
        if (thisC.getSafetyScore() > otherC.getSafetyScore()) betterInAtLeastOne = true;
        
        if (!betterInAtLeastOne) return false;
        
        // Check that we're not worse in any criterion
        return thisC.getTravelTime() <= otherC.getTravelTime()
            && thisC.getHighwayPenalty() <= otherC.getHighwayPenalty()
            && thisC.getTurnPenalty() <= otherC.getTurnPenalty()
            && thisC.getTollCost() <= otherC.getTollCost()
            && thisC.getScenicScore() >= otherC.getScenicScore()
            && thisC.getSafetyScore() >= otherC.getSafetyScore();
    }
    
    public RoutePath getRoute() { return route; }
    public boolean isDominated() { return dominated; }
    public void setDominated(boolean dominated) { this.dominated = dominated; }
}

