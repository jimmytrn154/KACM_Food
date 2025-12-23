package com.Chese.KACM_Recommendation.Controller;

import com.Chese.KACM_Recommendation.Service.KnowledgeGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller for Knowledge Graph-based food recommendations
 */
@RestController
@RequestMapping("/api/kg")
public class KnowledgeGraphController {
    
    @Autowired
    private KnowledgeGraphService kgService;
    
    /**
     * Check Neo4j connection status
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("connected", kgService.isConnected());
        return response;
    }
    
    /**
     * Initialize the knowledge graph (import all data)
     * POST /api/kg/initialize
     */
    @PostMapping("/initialize")
    public ResponseEntity<Map<String, String>> initializeGraph() {
        try {
            kgService.initializeKnowledgeGraph();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Knowledge graph initialized successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get recommendations based on diseases and preferences
     * POST /api/kg/recommend
     * Body: {
     *   "userId": "user123",
     *   "diseases": ["Diabetes", "Hypertension"],
     *   "allergies": ["Peanuts"],
     *   "limit": 10
     * }
     */
    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> getRecommendations(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.getOrDefault("userId", "temp_user_" + System.currentTimeMillis());
            @SuppressWarnings("unchecked")
            List<String> diseases = (List<String>) request.getOrDefault("diseases", new ArrayList<>());
            @SuppressWarnings("unchecked")
            List<String> allergies = (List<String>) request.getOrDefault("allergies", new ArrayList<>());
            int limit = ((Number) request.getOrDefault("limit", 10)).intValue();
            
            // Create user with diseases
            kgService.createUserWithDiseases(userId, diseases);
            
            // Get recommendations
            List<Map<String, Object>> recommendations = kgService.getRecommendationsByDijkstra(userId, limit);
            
            // Filter out allergies if specified
            if (!allergies.isEmpty()) {
                recommendations.removeIf(rec -> {
                    String foodName = (String) rec.get("food");
                    return allergies.stream().anyMatch(allergy -> 
                        foodName.toLowerCase().contains(allergy.toLowerCase())
                    );
                });
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("recommendations", recommendations);
            response.put("count", recommendations.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get food details from knowledge graph
     * GET /api/kg/food/{foodName}
     */
    @GetMapping("/food/{foodName}")
    public ResponseEntity<Map<String, Object>> getFoodDetails(@PathVariable String foodName) {
        try {
            Map<String, Object> details = kgService.getFoodDetails(foodName);
            if (details != null) {
                return ResponseEntity.ok(details);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get all available diseases
     * GET /api/kg/diseases
     */
    @GetMapping("/diseases")
    public ResponseEntity<?> getAllDiseases() {
        try {
            List<String> diseases = kgService.getAllDiseases();
            return ResponseEntity.ok(diseases);
        } catch (Exception e) {
            System.err.println("Error in getAllDiseases controller: " + e.getMessage());
            e.printStackTrace();
            // Return default diseases on error
            List<String> defaults = Arrays.asList(
                "Diabetes", "Hypertension", "Obesity", "Heart Disease", "High Cholesterol"
            );
            return ResponseEntity.ok(defaults);
        }
    }
    
    /**
     * Get all available foods
     * GET /api/kg/foods
     */
    @GetMapping("/foods")
    public ResponseEntity<List<String>> getAllFoods() {
        try {
            List<String> foods = kgService.getAllFoods();
            return ResponseEntity.ok(foods);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

