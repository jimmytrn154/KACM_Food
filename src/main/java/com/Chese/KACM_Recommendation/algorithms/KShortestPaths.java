package com.Chese.KACM_Recommendation.algorithms;

import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import com.Chese.KACM_Recommendation.model.RoutePath;
import com.Chese.KACM_Recommendation.model.RouteCriteria;

import java.util.*;

/**
 * K-Shortest Paths using Yen's Algorithm
 * Finds K distinct shortest paths between two nodes
 */
public class KShortestPaths {
    
    /**
     * Find K shortest paths from start to end
     * @param graph Road graph (from -> to -> weight)
     * @param start Starting node
     * @param end Destination node
     * @param k Number of paths to find
     * @param locations Map of node names to coordinates
     * @return List of K shortest paths
     */
    public static List<RoutePath> findKShortest(
            Map<String, Map<String, Double>> graph,
            String start,
            String end,
            int k,
            Map<String, LocationCoordinate> locations) {
        
        List<RoutePath> A = new ArrayList<>(); // List of k shortest paths
        List<RoutePath> B = new ArrayList<>(); // Candidate paths
        
        // Find shortest path
        RoutePath shortest = findShortestPath(graph, start, end, locations, Collections.emptySet());
        if (shortest == null) return A;
        
        A.add(shortest);
        
        for (int i = 1; i < k; i++) {
            RoutePath prevPath = A.get(i - 1);
            List<String> prevNodes = getNodeIds(prevPath.getPath(), locations);
            
            // For each node in the previous path (except the last)
            for (int j = 0; j < prevNodes.size() - 1; j++) {
                String spurNode = prevNodes.get(j);
                List<String> rootPath = prevNodes.subList(0, j + 1);
                
                // Remove edges used in root path from previous shortest paths
                Set<String> removedEdges = new HashSet<>();
                for (RoutePath path : A) {
                    List<String> pathNodes = getNodeIds(path.getPath(), locations);
                    if (pathNodes.size() > j + 1 && pathNodes.subList(0, j + 1).equals(rootPath)) {
                        String from = pathNodes.get(j);
                        String to = pathNodes.get(j + 1);
                        removedEdges.add(from + "->" + to);
                    }
                }
                
                // Find spur path
                RoutePath spurPath = findShortestPath(graph, spurNode, end, locations, removedEdges);
                
                if (spurPath != null) {
                    // Combine root path and spur path
                    List<LocationCoordinate> totalPath = new ArrayList<>();
                    for (int idx = 0; idx < rootPath.size() - 1; idx++) {
                        totalPath.add(locations.get(rootPath.get(idx)));
                    }
                    totalPath.addAll(spurPath.getPath());
                    
                    double totalTime = calculateTotalTime(graph, totalPath, locations);
                    RouteCriteria criteria = new RouteCriteria();
                    criteria.setTravelTime(totalTime);
                    
                    RoutePath totalPathObj = new RoutePath(totalPath, criteria, "Alternative " + (i + 1));
                    totalPathObj.setTotalTime(totalTime);
                    
                    if (!containsPath(B, totalPathObj)) {
                        B.add(totalPathObj);
                    }
                }
            }
            
            if (B.isEmpty()) break;
            
            // Sort candidates and add best to A
            B.sort(Comparator.comparingDouble(p -> p.getCriteria().getTravelTime()));
            A.add(B.remove(0));
        }
        
        return A;
    }
    
    private static RoutePath findShortestPath(
            Map<String, Map<String, Double>> graph,
            String start,
            String end,
            Map<String, LocationCoordinate> locations,
            Set<String> removedEdges) {
        
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        Set<String> visited = new HashSet<>();
        
        for (String node : graph.keySet()) {
            dist.put(node, Double.MAX_VALUE);
        }
        dist.put(start, 0.0);
        pq.add(start);
        
        while (!pq.isEmpty()) {
            String u = pq.poll();
            if (visited.contains(u)) continue;
            visited.add(u);
            
            if (u.equals(end)) {
                List<LocationCoordinate> path = reconstructPath(prev, end, locations);
                double totalTime = dist.get(end);
                RouteCriteria criteria = new RouteCriteria();
                criteria.setTravelTime(totalTime);
                RoutePath routePath = new RoutePath(path, criteria, "Shortest");
                routePath.setTotalTime(totalTime);
                return routePath;
            }
            
            Map<String, Double> neighbors = graph.get(u);
            if (neighbors == null) continue;
            
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                String v = entry.getKey();
                double weight = entry.getValue();
                
                String edge = u + "->" + v;
                if (removedEdges.contains(edge)) continue;
                
                if (!visited.contains(v)) {
                    double newDist = dist.get(u) + weight;
                    if (newDist < dist.get(v)) {
                        dist.put(v, newDist);
                        prev.put(v, u);
                        pq.add(v);
                    }
                }
            }
        }
        
        return null;
    }
    
    private static List<LocationCoordinate> reconstructPath(
            Map<String, String> prev, String end, Map<String, LocationCoordinate> locations) {
        List<LocationCoordinate> path = new LinkedList<>();
        String current = end;
        
        while (current != null) {
            path.add(0, locations.get(current));
            current = prev.get(current);
        }
        
        return path;
    }
    
    private static List<String> getNodeIds(List<LocationCoordinate> path, Map<String, LocationCoordinate> locations) {
        List<String> nodeIds = new ArrayList<>();
        for (LocationCoordinate coord : path) {
            for (Map.Entry<String, LocationCoordinate> entry : locations.entrySet()) {
                if (entry.getValue().equals(coord)) {
                    nodeIds.add(entry.getKey());
                    break;
                }
            }
        }
        return nodeIds;
    }
    
    private static double calculateTotalTime(
            Map<String, Map<String, Double>> graph,
            List<LocationCoordinate> path,
            Map<String, LocationCoordinate> locations) {
        
        double total = 0.0;
        List<String> nodeIds = getNodeIds(path, locations);
        
        for (int i = 0; i < nodeIds.size() - 1; i++) {
            String from = nodeIds.get(i);
            String to = nodeIds.get(i + 1);
            Map<String, Double> neighbors = graph.get(from);
            if (neighbors != null && neighbors.containsKey(to)) {
                total += neighbors.get(to);
            }
        }
        
        return total;
    }
    
    private static boolean containsPath(List<RoutePath> paths, RoutePath target) {
        for (RoutePath path : paths) {
            if (path.getPath().equals(target.getPath())) {
                return true;
            }
        }
        return false;
    }
}

