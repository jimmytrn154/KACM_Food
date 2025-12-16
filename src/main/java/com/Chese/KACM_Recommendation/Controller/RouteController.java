package com.Chese.KACM_Recommendation.Controller;

import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import com.Chese.KACM_Recommendation.model.RoutingPreferences;
import com.Chese.KACM_Recommendation.Service.RouteService;
import com.Chese.KACM_Recommendation.Service.AdvancedRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RouteController {

    @Autowired
    private RouteService routeService;
    
    @Autowired
    private AdvancedRouteService advancedRouteService;

    @GetMapping("/route")
    public List<LocationCoordinate> route(
            @RequestParam String from,
            @RequestParam String to) {
        return routeService.getShortestPath(from, to);
    }
    
    /**
     * 1. Time-dependent routing (traffic-aware)
     * GET /api/route/time-dependent?from=VinUniversity&to=f1&departureHour=8
     */
    @GetMapping("/route/time-dependent")
    public Map<String, Object> timeDependentRoute(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "12") int departureHour) {
        return advancedRouteService.getTimeDependentRoute(from, to, departureHour);
    }
    
    /**
     * 2. Customizable routing with CCH/CRP
     * POST /api/route/customizable
     * Body: { "from": "VinUniversity", "to": "f1", "preferences": {...} }
     */
    @PostMapping("/route/customizable")
    @SuppressWarnings("unchecked")
    public Map<String, Object> customizableRoute(@RequestBody Map<String, Object> request) {
        String from = (String) request.get("from");
        String to = (String) request.get("to");
        Map<String, Object> prefsMap = (Map<String, Object>) request.get("preferences");
        
        RoutingPreferences prefs = new RoutingPreferences();
        if (prefsMap != null) {
            prefs.setTimeWeight(((Number) prefsMap.getOrDefault("timeWeight", 1.0)).doubleValue());
            prefs.setHighwayWeight(((Number) prefsMap.getOrDefault("highwayWeight", 0.5)).doubleValue());
            prefs.setScenicWeight(((Number) prefsMap.getOrDefault("scenicWeight", 0.3)).doubleValue());
            prefs.setSafetyWeight(((Number) prefsMap.getOrDefault("safetyWeight", 0.4)).doubleValue());
            prefs.setTurnWeight(((Number) prefsMap.getOrDefault("turnWeight", 0.2)).doubleValue());
            prefs.setTollWeight(((Number) prefsMap.getOrDefault("tollWeight", 0.1)).doubleValue());
        }
        
        return advancedRouteService.getCustomizableRoute(from, to, prefs);
    }
    
    /**
     * 3. Top-K POI search (nearest restaurants)
     * GET /api/route/nearest-restaurants?from=VinUniversity&k=5
     */
    @GetMapping("/route/nearest-restaurants")
    public List<Map<String, Object>> nearestRestaurants(
            @RequestParam String from,
            @RequestParam(defaultValue = "5") int k) {
        return advancedRouteService.getNearestRestaurants(from, k);
    }
    
    /**
     * 4. Multi-criteria routing (Pareto-optimal paths)
     * GET /api/route/pareto?from=VinUniversity&to=f1
     */
    @GetMapping("/route/pareto")
    public List<Map<String, Object>> paretoOptimalRoutes(
            @RequestParam String from,
            @RequestParam String to) {
        return advancedRouteService.getParetoOptimalRoutes(from, to);
    }
    
    /**
     * 5. K-shortest paths
     * GET /api/route/k-shortest?from=VinUniversity&to=f1&k=3
     */
    @GetMapping("/route/k-shortest")
    public List<Map<String, Object>> kShortestPaths(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "3") int k) {
        return advancedRouteService.getKShortestPaths(from, to, k);
    }
    
    /**
     * Benchmark: Compare Dijkstra vs CCH
     * GET /api/route/benchmark?from=VinUniversity&to=f1
     */
    @GetMapping("/route/benchmark")
    public Map<String, Object> benchmark(
            @RequestParam String from,
            @RequestParam String to) {
        return advancedRouteService.benchmarkRouting(from, to);
    }
}