package com.Chese.KACM_Recommendation.algorithms;

import java.util.*;

/**
 * Simplified Contraction Hierarchy (CH) for fast routing queries
 * This is a simplified version - full CH is more complex
 */
public class ContractionHierarchy {
    
    private Map<String, Integer> nodeOrder; // Node -> order (importance)
    private Map<String, Map<String, Double>> upwardGraph; // Only edges to higher-ordered nodes
    private Map<String, Map<String, Double>> downwardGraph; // Only edges from higher-ordered nodes
    
    /**
     * Preprocess the graph (build hierarchy)
     * In full CH, this involves contracting nodes in order
     * Here we use a simplified heuristic: order by degree
     */
    public void preprocess(Map<String, Map<String, Double>> graph) {
        nodeOrder = new HashMap<>();
        upwardGraph = new HashMap<>();
        downwardGraph = new HashMap<>();
        
        // Calculate node degrees
        Map<String, Integer> degrees = new HashMap<>();
        for (String node : graph.keySet()) {
            degrees.put(node, graph.getOrDefault(node, Collections.emptyMap()).size());
        }
        
        // Order nodes by degree (simplified heuristic)
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(degrees.entrySet());
        sorted.sort((a, b) -> Integer.compare(a.getValue(), b.getValue()));
        
        int order = 0;
        for (Map.Entry<String, Integer> entry : sorted) {
            nodeOrder.put(entry.getKey(), order++);
        }
        
        // Build upward and downward graphs
        for (String u : graph.keySet()) {
            upwardGraph.put(u, new HashMap<>());
            downwardGraph.put(u, new HashMap<>());
            
            Map<String, Double> neighbors = graph.get(u);
            if (neighbors == null) continue;
            
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                String v = entry.getKey();
                double weight = entry.getValue();
                
                int uOrder = nodeOrder.get(u);
                int vOrder = nodeOrder.get(v);
                
                if (uOrder < vOrder) {
                    // u -> v is upward
                    upwardGraph.get(u).put(v, weight);
                } else {
                    // v -> u is downward (reverse edge)
                    downwardGraph.get(v).put(u, weight);
                }
            }
        }
    }
    
    /**
     * Customize: update edge weights based on preferences/traffic
     * This is fast because we only update weights, not rebuild the hierarchy
     */
    public void customize(RoutingCustomizer customizer) {
        if (customizer == null) return;
        // Update upward graph
        for (Map.Entry<String, Map<String, Double>> entry : upwardGraph.entrySet()) {
            String from = entry.getKey();
            Map<String, Double> neighbors = entry.getValue();
            for (String to : new HashSet<>(neighbors.keySet())) {
                double newWeight = customizer.customizeWeight(from, to, neighbors.get(to));
                neighbors.put(to, newWeight);
            }
        }
        
        // Update downward graph
        for (Map.Entry<String, Map<String, Double>> entry : downwardGraph.entrySet()) {
            String from = entry.getKey();
            Map<String, Double> neighbors = entry.getValue();
            for (String to : new HashSet<>(neighbors.keySet())) {
                double newWeight = customizer.customizeWeight(from, to, neighbors.get(to));
                neighbors.put(to, newWeight);
            }
        }
    }
    
    /**
     * Query: find shortest path using CH
     * CH query: forward search from start (upward), backward search from end (downward)
     * Meet in the middle at the highest-ordered node
     */
    public List<String> query(String start, String end) {
        // Forward search (upward from start)
        Map<String, Double> distForward = new HashMap<>();
        Map<String, String> prevForward = new HashMap<>();
        PriorityQueue<String> pqForward = new PriorityQueue<>(
            Comparator.comparingDouble(distForward::get)
        );
        
        distForward.put(start, 0.0);
        pqForward.add(start);
        
        while (!pqForward.isEmpty()) {
            String u = pqForward.poll();
            Map<String, Double> neighbors = upwardGraph.getOrDefault(u, Collections.emptyMap());
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                String v = entry.getKey();
                double weight = entry.getValue();
                double newDist = distForward.get(u) + weight;
                if (newDist < distForward.getOrDefault(v, Double.MAX_VALUE)) {
                    distForward.put(v, newDist);
                    prevForward.put(v, u);
                    pqForward.add(v);
                }
            }
        }
        
        // Backward search (downward from end)
        Map<String, Double> distBackward = new HashMap<>();
        Map<String, String> prevBackward = new HashMap<>();
        PriorityQueue<String> pqBackward = new PriorityQueue<>(
            Comparator.comparingDouble(distBackward::get)
        );
        
        distBackward.put(end, 0.0);
        pqBackward.add(end);
        
        while (!pqBackward.isEmpty()) {
            String u = pqBackward.poll();
            Map<String, Double> neighbors = downwardGraph.getOrDefault(u, Collections.emptyMap());
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                String v = entry.getKey();
                double weight = entry.getValue();
                double newDist = distBackward.get(u) + weight;
                if (newDist < distBackward.getOrDefault(v, Double.MAX_VALUE)) {
                    distBackward.put(v, newDist);
                    prevBackward.put(v, u);
                    pqBackward.add(v);
                }
            }
        }
        
        // Find meeting point (node with minimum distForward + distBackward)
        String meetingPoint = null;
        double minDist = Double.MAX_VALUE;
        Set<String> allNodes = new HashSet<>(distForward.keySet());
        allNodes.addAll(distBackward.keySet());
        
        for (String node : allNodes) {
            double forwardDist = distForward.getOrDefault(node, Double.MAX_VALUE);
            double backwardDist = distBackward.getOrDefault(node, Double.MAX_VALUE);
            if (forwardDist < Double.MAX_VALUE && backwardDist < Double.MAX_VALUE) {
                double total = forwardDist + backwardDist;
                if (total < minDist) {
                    minDist = total;
                    meetingPoint = node;
                }
            }
        }
        
        if (meetingPoint == null) return new ArrayList<>();
        
        // Reconstruct path
        List<String> path = new ArrayList<>();
        String current = meetingPoint;
        while (current != null) {
            path.add(0, current);
            current = prevForward.get(current);
        }
        current = prevBackward.get(meetingPoint);
        while (current != null) {
            path.add(current);
            current = prevBackward.get(current);
        }
        
        return path;
    }
    
    public interface RoutingCustomizer {
        double customizeWeight(String from, String to, double originalWeight);
    }
}

