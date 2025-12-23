package com.Chese.KACM_Recommendation.Service;

import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service to get actual road routes using OSRM (Open Source Routing Machine)
 * Returns real road paths instead of straight lines
 */
@Service
public class OSRMRoutingService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private final ObjectMapper objectMapper;
    private static final String OSRM_API = "http://router.project-osrm.org/route/v1/driving";
    
    public OSRMRoutingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get actual road route between two points using OSRM
     * @param from Starting location
     * @param to Destination location
     * @return List of LocationCoordinates representing the actual road path
     */
    public List<LocationCoordinate> getRoadRoute(LocationCoordinate from, LocationCoordinate to) {
        try {
            // OSRM format: longitude,latitude
            String coordinates = String.format("%.6f,%.6f;%.6f,%.6f", 
                from.getLon(), from.getLat(), 
                to.getLon(), to.getLat());
            
            String url = OSRM_API + "/" + coordinates + "?overview=full&geometries=geojson&steps=false";
            
            System.out.println("OSRM Request: " + url);
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null) {
                System.err.println("OSRM returned null response");
                return Arrays.asList(from, to);
            }
            
            System.out.println("OSRM Response length: " + response.length());
            
            JsonNode json = objectMapper.readTree(response);
            
            if (json.has("code")) {
                String code = json.get("code").asText();
                if (!code.equals("Ok")) {
                    System.err.println("OSRM error code: " + code);
                    if (json.has("message")) {
                        System.err.println("OSRM error message: " + json.get("message").asText());
                    }
                    return Arrays.asList(from, to);
                }
            }
            
            JsonNode routes = json.get("routes");
            if (routes != null && routes.isArray() && routes.size() > 0) {
                JsonNode route = routes.get(0);
                JsonNode geometry = route.get("geometry");
                
                if (geometry != null && geometry.has("coordinates")) {
                    JsonNode coords = geometry.get("coordinates");
                    
                    List<LocationCoordinate> path = new ArrayList<>();
                    for (JsonNode coord : coords) {
                        if (coord.isArray() && coord.size() >= 2) {
                            double lon = coord.get(0).asDouble();
                            double lat = coord.get(1).asDouble();
                            path.add(new LocationCoordinate("", lat, lon));
                        }
                    }
                    
                    // Set names for start and end points
                    if (!path.isEmpty()) {
                        path.get(0).setName(from.getName());
                        path.get(path.size() - 1).setName(to.getName());
                    }
                    
                    System.out.println("OSRM returned " + path.size() + " waypoints for route");
                    return path;
                } else {
                    System.err.println("OSRM response missing geometry/coordinates");
                }
            } else {
                System.err.println("OSRM response missing routes array");
            }
            
        } catch (Exception e) {
            System.err.println("Error getting OSRM route: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Fallback: return straight line if OSRM fails
        System.out.println("OSRM failed, returning direct path");
        return Arrays.asList(from, to);
    }
    
    /**
     * Get route with multiple waypoints (for multi-stop routes)
     */
    public List<LocationCoordinate> getRoadRouteWithWaypoints(List<LocationCoordinate> waypoints) {
        if (waypoints == null || waypoints.size() < 2) {
            return waypoints != null ? waypoints : new ArrayList<>();
        }
        
        try {
            // Build coordinate string for OSRM
            StringBuilder coordString = new StringBuilder();
            for (int i = 0; i < waypoints.size(); i++) {
                if (i > 0) coordString.append(";");
                coordString.append(String.format("%.6f,%.6f", 
                    waypoints.get(i).getLon(), 
                    waypoints.get(i).getLat()));
            }
            
            String url = OSRM_API + "/" + coordString.toString() + "?overview=full&geometries=geojson&steps=false";
            
            System.out.println("OSRM Request (waypoints): " + url);
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null) {
                System.err.println("OSRM returned null response for waypoints");
                return waypoints;
            }
            
            JsonNode json = objectMapper.readTree(response);
            
            if (json.has("code")) {
                String code = json.get("code").asText();
                if (!code.equals("Ok")) {
                    System.err.println("OSRM error code: " + code);
                    return waypoints;
                }
            }
            
            JsonNode routes = json.get("routes");
            if (routes != null && routes.isArray() && routes.size() > 0) {
                JsonNode route = routes.get(0);
                JsonNode geometry = route.get("geometry");
                
                if (geometry != null && geometry.has("coordinates")) {
                    JsonNode coords = geometry.get("coordinates");
                    
                    List<LocationCoordinate> path = new ArrayList<>();
                    for (JsonNode coord : coords) {
                        if (coord.isArray() && coord.size() >= 2) {
                            double lon = coord.get(0).asDouble();
                            double lat = coord.get(1).asDouble();
                            path.add(new LocationCoordinate("", lat, lon));
                        }
                    }
                    
                    // Set names for waypoints
                    if (!path.isEmpty() && waypoints.size() > 0) {
                        path.get(0).setName(waypoints.get(0).getName());
                        path.get(path.size() - 1).setName(waypoints.get(waypoints.size() - 1).getName());
                    }
                    
                    System.out.println("OSRM returned " + path.size() + " waypoints for multi-waypoint route");
                    return path;
                } else {
                    System.err.println("OSRM response missing geometry/coordinates");
                }
            } else {
                System.err.println("OSRM response missing routes array");
            }
            
        } catch (Exception e) {
            System.err.println("Error getting OSRM route with waypoints: " + e.getMessage());
        }
        
        // Fallback: return waypoints as-is
        return waypoints;
    }
    
    /**
     * Convert a simple path (straight lines) to actual road route
     * Takes a list of waypoints and returns detailed road path
     * This is the main method that ensures all routes use real roads
     */
    public List<LocationCoordinate> convertToRoadRoute(List<LocationCoordinate> waypoints) {
        if (waypoints == null || waypoints.size() < 2) {
            return waypoints != null ? waypoints : new ArrayList<>();
        }
        
        // If path already has many points, it might already be a road path
        // But we'll still verify by getting the route between start and end
        if (waypoints.size() > 10) {
            // Path might already be detailed, but ensure it's a real road route
            // Get route from first to last point to ensure it follows roads
            List<LocationCoordinate> verifiedPath = getRoadRoute(waypoints.get(0), waypoints.get(waypoints.size() - 1));
            if (verifiedPath != null && verifiedPath.size() > 2) {
                return verifiedPath;
            }
        }
        
        // If only 2 points, get direct route
        if (waypoints.size() == 2) {
            List<LocationCoordinate> roadPath = getRoadRoute(waypoints.get(0), waypoints.get(1));
            if (roadPath != null && roadPath.size() > 2) {
                return roadPath;
            }
            // If OSRM fails, return original (shouldn't happen often)
            return waypoints;
        }
        
        // For multiple waypoints, get route with all waypoints
        List<LocationCoordinate> roadPath = getRoadRouteWithWaypoints(waypoints);
        if (roadPath != null && roadPath.size() > waypoints.size()) {
            return roadPath;
        }
        
        // Fallback: if multi-waypoint fails, try direct route
        return getRoadRoute(waypoints.get(0), waypoints.get(waypoints.size() - 1));
    }
}

