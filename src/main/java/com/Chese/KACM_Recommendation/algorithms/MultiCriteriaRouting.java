package com.Chese.KACM_Recommendation.algorithms;

import com.Chese.KACM_Recommendation.model.*;

import java.util.*;

/**
 * Multi-criteria shortest path using label-setting algorithm
 * Returns Pareto-optimal routes (non-dominated solutions)
 */
public class MultiCriteriaRouting {
    
    /**
     * Find all Pareto-optimal paths from start to end
     * @param graph Multi-criteria graph (from -> to -> RouteCriteria)
     * @param start Starting node
     * @param end Destination node
     * @param locations Map of node names to coordinates
     * @return List of Pareto-optimal routes
     */
    public static List<ParetoRoute> findParetoOptimalPaths(
            Map<String, Map<String, RouteCriteria>> graph,
            String start,
            String end,
            Map<String, LocationCoordinate> locations) {
        
        // Label: (node, criteria, path)
        Map<String, List<Label>> labels = new HashMap<>();
        PriorityQueue<Label> pq = new PriorityQueue<>(
            Comparator.comparingDouble(l -> l.getCriteria().getTravelTime())
        );
        
        // Initialize
        RouteCriteria startCriteria = new RouteCriteria();
        Label startLabel = new Label(start, startCriteria, new ArrayList<>());
        labels.put(start, new ArrayList<>(List.of(startLabel)));
        pq.add(startLabel);
        
        while (!pq.isEmpty()) {
            Label current = pq.poll();
            String u = current.getNode();
            
            if (u.equals(end)) continue;
            
            Map<String, RouteCriteria> neighbors = graph.get(u);
            if (neighbors == null) continue;
            
            for (Map.Entry<String, RouteCriteria> entry : neighbors.entrySet()) {
                String v = entry.getKey();
                RouteCriteria edgeCriteria = entry.getValue();
                RouteCriteria newCriteria = current.getCriteria().add(edgeCriteria);
                
                List<String> newPath = new ArrayList<>(current.getPath());
                newPath.add(v);
                
                Label newLabel = new Label(v, newCriteria, newPath);
                
                // Check if this label is dominated
                List<Label> existingLabels = labels.getOrDefault(v, new ArrayList<>());
                boolean dominated = false;
                for (Label existing : existingLabels) {
                    if (dominates(existing.getCriteria(), newCriteria)) {
                        dominated = true;
                        break;
                    }
                }
                
                if (!dominated) {
                    // Remove labels dominated by new label
                    existingLabels.removeIf(l -> dominates(newCriteria, l.getCriteria()));
                    existingLabels.add(newLabel);
                    labels.put(v, existingLabels);
                    pq.add(newLabel);
                }
            }
        }
        
        // Convert to ParetoRoute objects
        List<ParetoRoute> paretoRoutes = new ArrayList<>();
        List<Label> endLabels = labels.getOrDefault(end, new ArrayList<>());
        
        for (Label label : endLabels) {
            List<LocationCoordinate> path = new ArrayList<>();
            path.add(locations.get(start));
            for (String node : label.getPath()) {
                path.add(locations.get(node));
            }
            RoutePath routePath = new RoutePath(path, label.getCriteria(), "Pareto-optimal");
            paretoRoutes.add(new ParetoRoute(routePath));
        }
        
        // Filter out dominated routes
        List<ParetoRoute> nonDominated = new ArrayList<>();
        for (ParetoRoute route : paretoRoutes) {
            boolean isDominated = false;
            for (ParetoRoute other : paretoRoutes) {
                if (route != other && other.dominates(route)) {
                    isDominated = true;
                    break;
                }
            }
            if (!isDominated) {
                nonDominated.add(route);
            }
        }
        
        return nonDominated;
    }
    
    /**
     * Check if criteria1 dominates criteria2
     * (criteria1 is better in at least one aspect and not worse in any)
     */
    private static boolean dominates(RouteCriteria c1, RouteCriteria c2) {
        boolean betterInAtLeastOne = false;
        
        if (c1.getTravelTime() < c2.getTravelTime()) betterInAtLeastOne = true;
        if (c1.getHighwayPenalty() < c2.getHighwayPenalty()) betterInAtLeastOne = true;
        if (c1.getTurnPenalty() < c2.getTurnPenalty()) betterInAtLeastOne = true;
        if (c1.getTollCost() < c2.getTollCost()) betterInAtLeastOne = true;
        if (c1.getScenicScore() > c2.getScenicScore()) betterInAtLeastOne = true;
        if (c1.getSafetyScore() > c2.getSafetyScore()) betterInAtLeastOne = true;
        
        if (!betterInAtLeastOne) return false;
        
        return c1.getTravelTime() <= c2.getTravelTime()
            && c1.getHighwayPenalty() <= c2.getHighwayPenalty()
            && c1.getTurnPenalty() <= c2.getTurnPenalty()
            && c1.getTollCost() <= c2.getTollCost()
            && c1.getScenicScore() >= c2.getScenicScore()
            && c1.getSafetyScore() >= c2.getSafetyScore();
    }
    
    private static class Label {
        private String node;
        private RouteCriteria criteria;
        private List<String> path;
        
        public Label(String node, RouteCriteria criteria, List<String> path) {
            this.node = node;
            this.criteria = criteria;
            this.path = path;
        }
        
        public String getNode() { return node; }
        public RouteCriteria getCriteria() { return criteria; }
        public List<String> getPath() { return path; }
    }
}

