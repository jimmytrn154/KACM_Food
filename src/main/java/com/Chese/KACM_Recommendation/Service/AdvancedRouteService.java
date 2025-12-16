package com.Chese.KACM_Recommendation.Service;

import com.Chese.KACM_Recommendation.Config.Restaurant;
import com.Chese.KACM_Recommendation.algorithms.*;
import com.Chese.KACM_Recommendation.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Advanced routing service with all modern features:
 * 1. Time-dependent routing (traffic-aware)
 * 2. CCH/CRP for fast customizable queries
 * 3. Top-K POI search
 * 4. Multi-criteria routing (Pareto-optimal)
 * 5. K-shortest paths
 */
@Service
public class AdvancedRouteService {
    
    private ContractionHierarchy ch;
    private Map<String, Map<String, TimeDependentEdge>> timeDependentGraph;
    private Map<String, Map<String, Double>> standardGraph;
    private Map<String, Map<String, RouteCriteria>> multiCriteriaGraph;
    private boolean chPreprocessed = false;
    
    /**
     * Initialize graphs from restaurant locations
     */
    private void initializeGraphs() {
        if (standardGraph != null) return; // Already initialized
        
        standardGraph = new HashMap<>();
        timeDependentGraph = new HashMap<>();
        multiCriteriaGraph = new HashMap<>();
        
        List<String> all = new ArrayList<>(Restaurant.locations.keySet());
        
        for (String u : all) {
            standardGraph.put(u, new HashMap<>());
            timeDependentGraph.put(u, new HashMap<>());
            multiCriteriaGraph.put(u, new HashMap<>());
            
            LocationCoordinate locU = Restaurant.locations.get(u);
            
            for (String v : all) {
                if (u.equals(v)) continue;
                
                LocationCoordinate locV = Restaurant.locations.get(v);
                double distance = haversine(locU.getLat(), locU.getLon(), locV.getLat(), locV.getLon());
                
                // Standard graph: weight = distance in km (convert to minutes assuming 50 km/h)
                double timeMinutes = (distance / 50.0) * 60;
                standardGraph.get(u).put(v, timeMinutes);
                
                // Time-dependent graph
                double baseSpeed = 50.0; // km/h
                TimeDependentEdge edge = new TimeDependentEdge(u, v, distance, baseSpeed);
                timeDependentGraph.get(u).put(v, edge);
                
                // Multi-criteria graph
                RouteCriteria criteria = new RouteCriteria();
                criteria.setTravelTime(timeMinutes);
                
                // Add some variety: some edges are highways, some are scenic, etc.
                if (distance > 2.0) {
                    criteria.setHighwayPenalty(0.8); // Long distance = likely highway
                    criteria.setTollCost(distance * 0.5); // Toll cost proportional to distance
                } else {
                    criteria.setScenicScore(0.6); // Short distance = scenic route
                    criteria.setSafetyScore(0.7);
                }
                
                // Random turn penalty (simplified)
                criteria.setTurnPenalty(Math.random() * 0.3);
                
                multiCriteriaGraph.get(u).put(v, criteria);
            }
        }
    }
    
    /**
     * 1. Time-dependent routing (traffic-aware)
     */
    public Map<String, Object> getTimeDependentRoute(String from, String to, int departureHour) {
        initializeGraphs();
        
        List<LocationCoordinate> path = TimeDependentDijkstra.findPath(
            timeDependentGraph, from, to, departureHour, Restaurant.locations
        );
        
        // Calculate congestion info
        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("departureHour", departureHour);
        result.put("congestionInfo", calculateCongestionInfo(path, departureHour));
        
        return result;
    }
    
    /**
     * 2. CCH/CRP routing with customizable preferences
     */
    public Map<String, Object> getCustomizableRoute(String from, String to, RoutingPreferences prefs) {
        initializeGraphs();
        
        // Preprocess CH if not done
        if (!chPreprocessed) {
            ch = new ContractionHierarchy();
            ch.preprocess(standardGraph);
            chPreprocessed = true;
        }
        
        // Customize based on preferences
        final RoutingPreferences finalPrefs = prefs;
        ch.customize((fromNode, toNode, originalWeight) -> {
            RouteCriteria criteria = multiCriteriaGraph.get(fromNode).get(toNode);
            if (criteria == null) return originalWeight;
            return criteria.getWeightedCost(finalPrefs);
        });
        
        // Query
        List<String> nodePath = ch.query(from, to);
        List<LocationCoordinate> path = nodePath.stream()
            .map(Restaurant.locations::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("method", "CCH");
        result.put("preferences", prefs);
        
        return result;
    }
    
    /**
     * 3. Top-K POI search (nearest restaurants by travel time)
     */
    public List<Map<String, Object>> getNearestRestaurants(String from, int k) {
        initializeGraphs();
        
        // Get all restaurant IDs (food IDs)
        Set<String> poiSet = Restaurant.locations.keySet().stream()
            .filter(key -> key.startsWith("f"))
            .collect(Collectors.toSet());
        
        List<TopKPOISearch.POIResult> results = TopKPOISearch.findNearest(
            standardGraph, from, poiSet, k, Restaurant.locations
        );
        
        return results.stream().map(poi -> {
            Map<String, Object> map = new HashMap<>();
            map.put("restaurantId", poi.getRestaurantId());
            map.put("travelTime", poi.getTravelTime());
            map.put("path", poi.getPath());
            return map;
        }).collect(Collectors.toList());
    }
    
    /**
     * 4. Multi-criteria routing (Pareto-optimal paths)
     */
    public List<Map<String, Object>> getParetoOptimalRoutes(String from, String to) {
        initializeGraphs();
        
        List<ParetoRoute> paretoRoutes = MultiCriteriaRouting.findParetoOptimalPaths(
            multiCriteriaGraph, from, to, Restaurant.locations
        );
        
        return paretoRoutes.stream().map(route -> {
            Map<String, Object> map = new HashMap<>();
            map.put("path", route.getRoute().getPath());
            map.put("criteria", route.getRoute().getCriteria());
            map.put("description", route.getRoute().getDescription());
            return map;
        }).collect(Collectors.toList());
    }
    
    /**
     * 5. K-shortest paths
     */
    public List<Map<String, Object>> getKShortestPaths(String from, String to, int k) {
        initializeGraphs();
        
        List<RoutePath> paths = KShortestPaths.findKShortest(
            standardGraph, from, to, k, Restaurant.locations
        );
        
        return paths.stream().map(path -> {
            Map<String, Object> map = new HashMap<>();
            map.put("path", path.getPath());
            map.put("travelTime", path.getCriteria().getTravelTime());
            map.put("description", path.getDescription());
            return map;
        }).collect(Collectors.toList());
    }
    
    /**
     * Compare Dijkstra vs CCH query time
     */
    public Map<String, Object> benchmarkRouting(String from, String to) {
        initializeGraphs();
        
        long startTime, endTime;
        
        // Dijkstra
        startTime = System.nanoTime();
        List<LocationCoordinate> dijkstraPath = getShortestPathDijkstra(from, to);
        endTime = System.nanoTime();
        long dijkstraTime = (endTime - startTime) / 1000; // microseconds
        
        // CCH
        if (!chPreprocessed) {
            ch = new ContractionHierarchy();
            ch.preprocess(standardGraph);
            chPreprocessed = true;
        }
        startTime = System.nanoTime();
        List<String> chPath = ch.query(from, to);
        endTime = System.nanoTime();
        long chTime = (endTime - startTime) / 1000; // microseconds
        
        Map<String, Object> result = new HashMap<>();
        result.put("dijkstraTime", dijkstraTime);
        result.put("chTime", chTime);
        result.put("speedup", (double) dijkstraTime / chTime);
        result.put("dijkstraPath", dijkstraPath);
        result.put("chPath", chPath.stream().map(Restaurant.locations::get).collect(Collectors.toList()));
        
        return result;
    }
    
    private List<LocationCoordinate> getShortestPathDijkstra(String from, String to) {
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        Set<String> visited = new HashSet<>();
        
        for (String node : standardGraph.keySet()) {
            dist.put(node, Double.MAX_VALUE);
        }
        dist.put(from, 0.0);
        pq.add(from);
        
        while (!pq.isEmpty()) {
            String u = pq.poll();
            if (visited.contains(u)) continue;
            visited.add(u);
            
            if (u.equals(to)) {
                List<LocationCoordinate> path = new LinkedList<>();
                String current = to;
                while (current != null) {
                    path.add(0, Restaurant.locations.get(current));
                    current = prev.get(current);
                }
                return path;
            }
            
            Map<String, Double> neighbors = standardGraph.get(u);
            if (neighbors == null) continue;
            
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                String v = entry.getKey();
                double weight = entry.getValue();
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
        
        return new ArrayList<>();
    }
    
    private Map<String, Object> calculateCongestionInfo(List<LocationCoordinate> path, int departureHour) {
        Map<String, Object> info = new HashMap<>();
        int congestedSegments = 0;
        double totalCongestionPenalty = 0;
        
        if (path.size() < 2) {
            info.put("congestedSegments", 0);
            info.put("totalCongestionPenalty", 0);
            return info;
        }
        
        // Check each segment
        for (int i = 0; i < path.size() - 1; i++) {
            // Simplified: check if segment is in rush hour
            int currentHour = (departureHour + i) % 24;
            if ((currentHour >= 7 && currentHour <= 9) || (currentHour >= 17 && currentHour <= 19)) {
                congestedSegments++;
                totalCongestionPenalty += 0.4; // 40% penalty during rush hour
            }
        }
        
        info.put("congestedSegments", congestedSegments);
        info.put("totalCongestionPenalty", totalCongestionPenalty);
        info.put("rushHour", (departureHour >= 7 && departureHour <= 9) || (departureHour >= 17 && departureHour <= 19));
        
        return info;
    }
    
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                 + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon/2)*Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}

