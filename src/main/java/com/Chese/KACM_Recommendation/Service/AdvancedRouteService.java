package com.Chese.KACM_Recommendation.Service;

import com.Chese.KACM_Recommendation.Config.Restaurant;
import com.Chese.KACM_Recommendation.algorithms.*;
import com.Chese.KACM_Recommendation.model.*;
import org.springframework.beans.factory.annotation.Autowired;
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
                // For scenic routes: prefer longer paths through scenic areas
                if (distance > 2.0) {
                    // Long distance = likely highway (avoid when scenic preference is high)
                    criteria.setHighwayPenalty(0.8); 
                    criteria.setTollCost(distance * 0.5); // Toll cost proportional to distance
                    criteria.setScenicScore(0.1); // Highways are not scenic
                } else if (distance > 1.0 && distance <= 2.0) {
                    // Medium distance = moderate scenic potential
                    criteria.setScenicScore(0.4);
                    criteria.setSafetyScore(0.6);
                    criteria.setHighwayPenalty(0.3);
                } else {
                    // Short distance = very scenic route (local roads, parks, etc.)
                    criteria.setScenicScore(0.8); // High scenic score
                    criteria.setSafetyScore(0.7);
                    criteria.setHighwayPenalty(0.1); // Not a highway
                }
                
                // Add some randomness to make routes vary
                // Scenic routes should prefer paths with multiple scenic segments
                criteria.setTurnPenalty(Math.random() * 0.2); // Lower turn penalty for scenic routes
                
                multiCriteriaGraph.get(u).put(v, criteria);
            }
        }
    }
    
    @Autowired
    private OSRMRoutingService osrmRoutingService;
    
    /**
     * 1. Time-dependent routing (traffic-aware)
     */
    public Map<String, Object> getTimeDependentRoute(String from, String to, int departureHour) {
        initializeGraphs();
        
        List<LocationCoordinate> path = TimeDependentDijkstra.findPath(
            timeDependentGraph, from, to, departureHour, Restaurant.locations
        );
        
        // Convert to actual road route using OSRM
        if (path.size() >= 2) {
            try {
                List<LocationCoordinate> roadPath = osrmRoutingService.convertToRoadRoute(path);
                if (roadPath != null && roadPath.size() > 2) {
                    path = roadPath;
                    System.out.println("OSRM route converted: " + path.size() + " waypoints");
                } else {
                    System.out.println("OSRM returned insufficient points, using direct path");
                }
            } catch (Exception e) {
                System.err.println("OSRM routing failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Calculate congestion info with segment details
        Map<String, Object> congestionInfo = calculateCongestionInfo(path, departureHour);
        List<Map<String, Object>> trafficSegments = calculateTrafficSegments(path, departureHour);
        
        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("departureHour", departureHour);
        result.put("congestionInfo", congestionInfo);
        result.put("trafficInfo", Map.of("segments", trafficSegments));
        
        return result;
    }
    
    /**
     * Convert path to actual road route using OSRM
     */
    private List<LocationCoordinate> convertPathToRoadRoute(List<LocationCoordinate> path) {
        if (path != null && path.size() >= 2) {
            try {
                List<LocationCoordinate> roadPath = osrmRoutingService.convertToRoadRoute(path);
                if (roadPath != null && roadPath.size() > 2) {
                    System.out.println("Converted path from " + path.size() + " to " + roadPath.size() + " waypoints");
                    return roadPath;
                } else {
                    System.out.println("OSRM returned insufficient points (" + 
                        (roadPath != null ? roadPath.size() : 0) + "), using original path");
                }
            } catch (Exception e) {
                System.err.println("OSRM routing failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return path;
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
            
            double cost = criteria.getWeightedCost(finalPrefs);
            
            // When scenicWeight is high, make the algorithm prefer longer paths
            // by reducing cost of scenic edges more aggressively
            if (finalPrefs.getScenicWeight() > 1.0 && criteria.getScenicScore() > 0.5) {
                // Heavily discount scenic edges to encourage longer scenic routes
                double scenicDiscount = (finalPrefs.getScenicWeight() - 1.0) * 0.5;
                cost = cost * (1.0 - scenicDiscount);
            }
            
            // When scenicWeight is high, heavily penalize highways to force scenic routes
            if (finalPrefs.getScenicWeight() > 1.0 && criteria.getHighwayPenalty() > 0.5) {
                // Make highways very expensive
                double highwayPenalty = (finalPrefs.getScenicWeight() - 1.0) * 1.5;
                cost = cost * (1.0 + highwayPenalty);
            }
            
            return Math.max(0.1, cost); // Ensure cost is always positive
        });
        
        // Query
        List<String> nodePath = ch.query(from, to);
        List<LocationCoordinate> path = nodePath.stream()
            .map(Restaurant.locations::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        // ALWAYS convert to actual road route using OSRM (even if path has only 2 points)
        // This ensures the route follows real roads, not straight lines
        if (path.size() >= 2 && osrmRoutingService != null) {
            try {
                System.out.println("CCH: Converting path with " + path.size() + " waypoints to real road route");
                List<LocationCoordinate> roadPath = osrmRoutingService.convertToRoadRoute(path);
                if (roadPath != null && roadPath.size() >= 2) {
                    System.out.println("CCH: Successfully converted to " + roadPath.size() + " waypoints (real road path)");
                    path = roadPath;
                } else {
                    System.err.println("CCH: OSRM returned null or insufficient points (" + 
                        (roadPath != null ? roadPath.size() : 0) + "), using original path");
                }
            } catch (Exception e) {
                System.err.println("CCH: OSRM routing failed: " + e.getMessage());
                e.printStackTrace();
                // Continue with original path if OSRM fails
            }
        } else if (osrmRoutingService == null) {
            System.err.println("CCH: OSRMRoutingService is not available, using original path");
        }
        
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
            // Convert path to actual road route
            List<LocationCoordinate> roadPath = convertPathToRoadRoute(poi.getPath());
            map.put("path", roadPath);
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
            // Convert path to actual road route
            List<LocationCoordinate> roadPath = convertPathToRoadRoute(route.getRoute().getPath());
            map.put("path", roadPath);
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
            // Convert path to actual road route
            List<LocationCoordinate> roadPath = convertPathToRoadRoute(path.getPath());
            map.put("path", roadPath);
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
    
    /**
     * Calculate traffic segments for visualization
     */
    private List<Map<String, Object>> calculateTrafficSegments(List<LocationCoordinate> path, int departureHour) {
        List<Map<String, Object>> segments = new ArrayList<>();
        
        if (path.size() < 2) return segments;
        
        boolean isRushHour = (departureHour >= 7 && departureHour <= 9) || 
                            (departureHour >= 17 && departureHour <= 19);
        
        // Divide path into segments (every 2-3 points)
        int segmentSize = Math.max(2, path.size() / 5); // 5 segments max
        
        for (int i = 0; i < path.size() - 1; i += segmentSize) {
            int endIndex = Math.min(i + segmentSize, path.size() - 1);
            
            // Calculate congestion for this segment
            double congestion = 0.0;
            if (isRushHour) {
                // Higher congestion during rush hour
                congestion = 0.3 + Math.random() * 0.5; // 30-80% congestion
            } else {
                // Lower congestion during normal hours
                congestion = Math.random() * 0.3; // 0-30% congestion
            }
            
            Map<String, Object> segmentInfo = new HashMap<>();
            segmentInfo.put("length", endIndex - i + 1);
            segmentInfo.put("congestion", congestion);
            segmentInfo.put("startIndex", i);
            segmentInfo.put("endIndex", endIndex);
            segments.add(segmentInfo);
        }
        
        return segments;
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

