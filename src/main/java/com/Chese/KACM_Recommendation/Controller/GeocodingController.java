package com.Chese.KACM_Recommendation.Controller;

import com.Chese.KACM_Recommendation.Service.GeocodingService;
import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import com.Chese.KACM_Recommendation.Config.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller for geocoding restaurants and getting real positions
 */
@RestController
@RequestMapping("/api/geocode")
public class GeocodingController {
    
    @Autowired
    private GeocodingService geocodingService;
    
    /**
     * Geocode a single restaurant
     * GET /api/geocode/restaurant?name=Pho24&city=Gia Lâm
     */
    @GetMapping("/restaurant")
    public ResponseEntity<Map<String, Object>> geocodeRestaurant(
            @RequestParam String name,
            @RequestParam(required = false) String city) {
        
        LocationCoordinate coord = geocodingService.geocodeRestaurant(name, city);
        
        Map<String, Object> response = new HashMap<>();
        if (coord != null) {
            response.put("success", true);
            response.put("name", coord.getName());
            response.put("lat", coord.getLat());
            response.put("lon", coord.getLon());
        } else {
            response.put("success", false);
            response.put("message", "Restaurant not found");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get real positions for all restaurants in the system
     * POST /api/geocode/all-restaurants
     * This will update Restaurant.locations with real coordinates
     */
    @PostMapping("/all-restaurants")
    public ResponseEntity<Map<String, Object>> geocodeAllRestaurants() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        
        // Get all restaurant names from current locations
        Map<String, String> restaurantNames = new HashMap<>();
        for (Map.Entry<String, LocationCoordinate> entry : Restaurant.locations.entrySet()) {
            if (!entry.getKey().equals("VinUniversity")) {
                restaurantNames.put(entry.getKey(), entry.getValue().getName());
            }
        }
        
        // Geocode each restaurant
        for (Map.Entry<String, String> entry : restaurantNames.entrySet()) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", entry.getKey());
            result.put("name", entry.getValue());
            
            LocationCoordinate coord = geocodingService.geocodeRestaurantWithFallback(entry.getValue());
            
            if (coord != null) {
                // Update the location in Restaurant.locations
                Restaurant.locations.put(entry.getKey(), coord);
                result.put("success", true);
                result.put("lat", coord.getLat());
                result.put("lon", coord.getLon());
                successCount++;
            } else {
                result.put("success", false);
                result.put("message", "Not found");
                failCount++;
            }
            
            results.add(result);
            
            // Rate limiting
            try {
                Thread.sleep(1100); // 1.1 seconds between requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        response.put("total", restaurantNames.size());
        response.put("success", successCount);
        response.put("failed", failCount);
        response.put("results", results);
        response.put("message", "Geocoding complete. " + successCount + " restaurants found, " + failCount + " not found.");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current positions of all restaurants
     * GET /api/geocode/current-positions
     */
    @GetMapping("/current-positions")
    public ResponseEntity<Map<String, Object>> getCurrentPositions() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> restaurants = new ArrayList<>();
        
        for (Map.Entry<String, LocationCoordinate> entry : Restaurant.locations.entrySet()) {
            Map<String, Object> restaurant = new HashMap<>();
            restaurant.put("id", entry.getKey());
            restaurant.put("name", entry.getValue().getName());
            restaurant.put("lat", entry.getValue().getLat());
            restaurant.put("lon", entry.getValue().getLon());
            restaurants.add(restaurant);
        }
        
        response.put("count", restaurants.size());
        response.put("restaurants", restaurants);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update a specific restaurant's position
     * POST /api/geocode/update-restaurant
     * Body: { "id": "f1", "name": "Pho24" }
     */
    @PostMapping("/update-restaurant")
    public ResponseEntity<Map<String, Object>> updateRestaurant(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        String name = request.get("name");
        
        Map<String, Object> response = new HashMap<>();
        
        if (id == null || name == null) {
            response.put("success", false);
            response.put("message", "id and name are required");
            return ResponseEntity.badRequest().body(response);
        }
        
        LocationCoordinate coord = geocodingService.geocodeRestaurantWithFallback(name);
        
        if (coord != null) {
            Restaurant.locations.put(id, coord);
            response.put("success", true);
            response.put("id", id);
            response.put("name", coord.getName());
            response.put("lat", coord.getLat());
            response.put("lon", coord.getLon());
        } else {
            response.put("success", false);
            response.put("message", "Restaurant not found");
        }
        
        return ResponseEntity.ok(response);
    }
}

