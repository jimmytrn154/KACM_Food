package com.Chese.KACM_Recommendation.algorithms;

import com.Chese.KACM_Recommendation.model.LocationCoordinate;

import java.util.*;

/**
 * Top-K POI (Point of Interest) Search
 * Finds K nearest restaurants by travel time (not Euclidean distance)
 */
public class TopKPOISearch {
    
    /**
     * Find K nearest restaurants from a starting point
     * @param graph Road graph (from -> to -> weight in minutes)
     * @param start Starting location
     * @param poiSet Set of restaurant node IDs
     * @param k Number of restaurants to find
     * @param locations Map of node names to coordinates
     * @return List of (restaurantId, travelTime, path) tuples, sorted by travel time
     */
    public static List<POIResult> findNearest(
            Map<String, Map<String, Double>> graph,
            String start,
            Set<String> poiSet,
            int k,
            Map<String, LocationCoordinate> locations) {
        
        PriorityQueue<SearchState> pq = new PriorityQueue<>(Comparator.comparingDouble(SearchState::getTravelTime));
        Map<String, Double> bestTime = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        List<POIResult> results = new ArrayList<>();
        Set<String> foundPOIs = new HashSet<>();
        Set<String> explored = new HashSet<>();
        
        // Initialize
        for (String node : graph.keySet()) {
            bestTime.put(node, Double.MAX_VALUE);
        }
        bestTime.put(start, 0.0);
        pq.add(new SearchState(start, 0.0));
        
        while (!pq.isEmpty() && foundPOIs.size() < k) {
            SearchState current = pq.poll();
            String u = current.getNode();
            double travelTime = current.getTravelTime();
            
            if (explored.contains(u)) continue;
            explored.add(u);
            
            // Check if this is a POI
            if (poiSet.contains(u) && !foundPOIs.contains(u)) {
                foundPOIs.add(u);
                List<LocationCoordinate> path = reconstructPath(prev, u, start, locations);
                results.add(new POIResult(u, travelTime, path));
            }
            
            Map<String, Double> neighbors = graph.get(u);
            if (neighbors == null) continue;
            
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                String v = entry.getKey();
                double edgeWeight = entry.getValue();
                
                if (explored.contains(v)) continue;
                
                double newTime = travelTime + edgeWeight;
                if (newTime < bestTime.get(v)) {
                    bestTime.put(v, newTime);
                    prev.put(v, u);
                    pq.add(new SearchState(v, newTime));
                }
            }
        }
        
        // Sort by travel time
        results.sort(Comparator.comparingDouble(POIResult::getTravelTime));
        return results;
    }
    
    private static List<LocationCoordinate> reconstructPath(
            Map<String, String> prev, String end, String start, Map<String, LocationCoordinate> locations) {
        List<LocationCoordinate> path = new LinkedList<>();
        String current = end;
        
        while (current != null) {
            path.add(0, locations.get(current));
            current = prev.get(current);
            if (current != null && current.equals(start)) {
                path.add(0, locations.get(start));
                break;
            }
        }
        
        return path;
    }
    
    private static class SearchState {
        private String node;
        private double travelTime;
        
        public SearchState(String node, double travelTime) {
            this.node = node;
            this.travelTime = travelTime;
        }
        
        public String getNode() { return node; }
        public double getTravelTime() { return travelTime; }
    }
    
    public static class POIResult {
        private String restaurantId;
        private double travelTime;
        private List<LocationCoordinate> path;
        
        public POIResult(String restaurantId, double travelTime, List<LocationCoordinate> path) {
            this.restaurantId = restaurantId;
            this.travelTime = travelTime;
            this.path = path;
        }
        
        public String getRestaurantId() { return restaurantId; }
        public double getTravelTime() { return travelTime; }
        public List<LocationCoordinate> getPath() { return path; }
    }
}

