package com.Chese.KACM_Recommendation.algorithms;

import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import com.Chese.KACM_Recommendation.model.TimeDependentEdge;

import java.util.*;

/**
 * Time-Dependent Dijkstra's Algorithm
 * Handles traffic-aware routing where edge weights depend on departure time
 */
public class TimeDependentDijkstra {
    
    /**
     * Find shortest path with time-dependent weights
     * @param graph Map of from -> Map of to -> TimeDependentEdge
     * @param start Starting node
     * @param end Destination node
     * @param departureHour Hour of departure (0-23)
     * @param locations Map of node names to coordinates
     * @return List of LocationCoordinates representing the path
     */
    public static List<LocationCoordinate> findPath(
            Map<String, Map<String, TimeDependentEdge>> graph,
            String start,
            String end,
            int departureHour,
            Map<String, LocationCoordinate> locations) {
        
        // Priority queue: (node, arrivalTime)
        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingDouble(State::getArrivalTime));
        Map<String, Double> bestTime = new HashMap<>();
        Map<String, State> prev = new HashMap<>();
        
        // Initialize
        for (String node : graph.keySet()) {
            bestTime.put(node, Double.MAX_VALUE);
        }
        bestTime.put(start, departureHour * 60.0); // Convert hour to minutes
        pq.add(new State(start, departureHour * 60.0));
        
        while (!pq.isEmpty()) {
            State current = pq.poll();
            String u = current.getNode();
            double arrivalTime = current.getArrivalTime();
            
            // Skip if we've found a better path to this node
            if (arrivalTime > bestTime.get(u)) continue;
            
            if (u.equals(end)) {
                // Reconstruct path
                return reconstructPath(prev, end, locations);
            }
            
            Map<String, TimeDependentEdge> neighbors = graph.get(u);
            if (neighbors == null) continue;
            
            for (Map.Entry<String, TimeDependentEdge> entry : neighbors.entrySet()) {
                String v = entry.getKey();
                TimeDependentEdge edge = entry.getValue();
                
                // Calculate departure hour from current arrival time
                int depHour = (int) (arrivalTime / 60) % 24;
                double travelTime = edge.getTravelTime(depHour);
                double newArrivalTime = arrivalTime + travelTime;
                
                if (newArrivalTime < bestTime.get(v)) {
                    bestTime.put(v, newArrivalTime);
                    State newState = new State(v, newArrivalTime);
                    prev.put(v, current);
                    pq.add(newState);
                }
            }
        }
        
        return new ArrayList<>(); // No path found
    }
    
    private static List<LocationCoordinate> reconstructPath(
            Map<String, State> prev, String end, Map<String, LocationCoordinate> locations) {
        List<LocationCoordinate> path = new LinkedList<>();
        State current = prev.get(end);
        path.add(0, locations.get(end));
        
        while (current != null) {
            path.add(0, locations.get(current.getNode()));
            current = prev.get(current.getNode());
        }
        
        return path;
    }
    
    private static class State {
        private String node;
        private double arrivalTime;
        
        public State(String node, double arrivalTime) {
            this.node = node;
            this.arrivalTime = arrivalTime;
        }
        
        public String getNode() { return node; }
        public double getArrivalTime() { return arrivalTime; }
    }
}

