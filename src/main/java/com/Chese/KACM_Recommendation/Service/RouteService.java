package com.Chese.KACM_Recommendation.Service;

import com.Chese.KACM_Recommendation.Config.Restaurant;
import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteService {
    
    @Autowired
    private OSRMRoutingService osrmRoutingService;
    
    public List<LocationCoordinate> getShortestPath(String startKey, String endKey) {
        // build complete graph of keys -> weights (haversine distance)
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        List<String> all = new ArrayList<>(Restaurant.locations.keySet());
        for (String u : all) {
            Map<String, Integer> nbrs = new HashMap<>();
            for (String v : all) {
                if (!u.equals(v)) {
                    LocationCoordinate a = Restaurant.locations.get(u);
                    LocationCoordinate b = Restaurant.locations.get(v);
                    int d = (int)Math.round(haversine(a.getLat(), a.getLon(), b.getLat(), b.getLon()));
                    nbrs.put(v, d);
                }
            }
            graph.put(u, nbrs);
        }

        // Dijkstra
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        Set<String> seen = new HashSet<>();
        for (String k : all) dist.put(k, Integer.MAX_VALUE);
        dist.put(startKey, 0);
        pq.add(startKey);

        while (!pq.isEmpty()) {
            String u = pq.poll();
            if (seen.contains(u)) continue;
            seen.add(u);
            for (var e : graph.get(u).entrySet()) {
                String v = e.getKey(); int w = e.getValue();
                if (seen.contains(v)) continue;
                int nd = dist.get(u) + w;
                if (nd < dist.get(v)) {
                    dist.put(v, nd);
                    prev.put(v, u);
                    pq.add(v);
                }
            }
        }

        // reconstruct
        LinkedList<LocationCoordinate> path = new LinkedList<>();
        String cur = endKey;
        while (cur != null) {
            path.addFirst(Restaurant.locations.get(cur));
            cur = prev.get(cur);
        }
        
        // Convert to actual road route using OSRM
        if (path.size() >= 2) {
            try {
                List<LocationCoordinate> roadPath = osrmRoutingService.convertToRoadRoute(path);
                if (roadPath != null && roadPath.size() > 2) {
                    System.out.println("RouteService: Converted to OSRM route with " + roadPath.size() + " waypoints");
                    return roadPath;
                } else {
                    System.out.println("RouteService: OSRM returned insufficient points, using direct path");
                }
            } catch (Exception e) {
                System.err.println("RouteService: OSRM routing failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return path;
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