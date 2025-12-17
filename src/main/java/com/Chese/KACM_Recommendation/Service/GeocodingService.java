package com.Chese.KACM_Recommendation.Service;

import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to get real coordinates for restaurants using geocoding APIs
 * Uses OpenStreetMap Nominatim API (free, no API key required)
 */
@Service
public class GeocodingService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/search";
    
    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get coordinates for a restaurant name near VinUniversity
     * @param restaurantName Name of the restaurant
     * @param city City name (default: Gia Lâm, Hanoi)
     * @return LocationCoordinate with real coordinates, or null if not found
     */
    public LocationCoordinate geocodeRestaurant(String restaurantName, String city) {
        try {
            // Build search query: restaurant name + city + Vietnam
            String query = restaurantName + ", " + (city != null ? city : "Gia Lâm") + ", Hanoi, Vietnam";
            String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
            
            // Call Nominatim API
            String url = NOMINATIM_API + "?q=" + encodedQuery + 
                        "&format=json&limit=1&addressdetails=1";
            
            // Add user agent header (required by Nominatim)
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "KACM-Food-Recommendation/1.0");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
            
            // Use RestTemplate with headers
            org.springframework.http.ResponseEntity<String> responseEntity = restTemplate.exchange(
                url, 
                org.springframework.http.HttpMethod.GET, 
                entity, 
                String.class
            );
            String response = responseEntity.getBody();
            
            if (response != null && response.startsWith("[")) {
                JsonNode results = objectMapper.readTree(response);
                
                if (results.isArray() && results.size() > 0) {
                    JsonNode firstResult = results.get(0);
                    double lat = firstResult.get("lat").asDouble();
                    double lon = firstResult.get("lon").asDouble();
                    
                    return new LocationCoordinate(restaurantName, lat, lon);
                }
            }
            
            System.out.println("No results found for: " + restaurantName);
            return null;
            
        } catch (Exception e) {
            System.err.println("Error geocoding " + restaurantName + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get coordinates for a restaurant with multiple search attempts
     * Tries different variations of the restaurant name
     */
    public LocationCoordinate geocodeRestaurantWithFallback(String restaurantName) {
        // Try different search variations
        String[] variations = {
            restaurantName + " Gia Lâm",
            restaurantName + " Hanoi",
            restaurantName + " Vietnam",
            restaurantName, // Try original name
            restaurantName + " Đa Tốn", // Common area name
            restaurantName + " Vinhomes Ocean Park" // Nearby area
        };
        
        for (String variation : variations) {
            LocationCoordinate result = geocodeRestaurant(variation, null);
            if (result != null) {
                // Verify it's in the right area (near VinUniversity)
                double vinLat = 20.98892;
                double vinLon = 105.94601;
                double distance = haversine(vinLat, vinLon, result.getLat(), result.getLon());
                
                // Accept if within 10km of VinUniversity
                if (distance <= 10.0) {
                    return result;
                }
            }
        }
        
        // If not found, return approximate location near VinUniversity
        // This ensures all restaurants have a position
        System.out.println("Restaurant not found in OSM, using approximate location: " + restaurantName);
        return getApproximateLocation(restaurantName);
    }
    
    /**
     * Get approximate location near VinUniversity for restaurants not found in OSM
     */
    private LocationCoordinate getApproximateLocation(String restaurantName) {
        // Generate a location within 2km of VinUniversity
        double vinLat = 20.98892;
        double vinLon = 105.94601;
        
        // Use hash of restaurant name to get consistent offset
        int hash = restaurantName.hashCode();
        double latOffset = (hash % 2000) / 100000.0; // ~0-0.02 degrees (~2km)
        double lonOffset = ((hash / 2000) % 2000) / 100000.0;
        
        // Ensure positive offsets
        if (latOffset < 0) latOffset = -latOffset;
        if (lonOffset < 0) lonOffset = -lonOffset;
        
        return new LocationCoordinate(restaurantName, 
            vinLat + latOffset - 0.01, // Center around VinUniversity
            vinLon + lonOffset - 0.01);
    }
    
    /**
     * Haversine formula to calculate distance between two points
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    /**
     * Batch geocode multiple restaurants
     * @param restaurantNames Map of restaurant IDs to names
     * @return Map of restaurant IDs to LocationCoordinates
     */
    public Map<String, LocationCoordinate> batchGeocode(Map<String, String> restaurantNames) {
        Map<String, LocationCoordinate> results = new HashMap<>();
        
        for (Map.Entry<String, String> entry : restaurantNames.entrySet()) {
            System.out.println("Geocoding: " + entry.getValue());
            LocationCoordinate coord = geocodeRestaurantWithFallback(entry.getValue());
            
            if (coord != null) {
                results.put(entry.getKey(), coord);
                System.out.println("Found: " + coord.getName() + " at " + coord.getLat() + ", " + coord.getLon());
            } else {
                System.out.println("Not found: " + entry.getValue());
            }
            
            // Rate limiting: Nominatim allows 1 request per second
            try {
                Thread.sleep(1100); // Wait 1.1 seconds between requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        return results;
    }
    
    /**
     * Get coordinates using Google Places API (if API key is provided)
     * Requires GOOGLE_PLACES_API_KEY environment variable
     */
    public LocationCoordinate geocodeWithGooglePlaces(String restaurantName, String city) {
        String apiKey = System.getenv("GOOGLE_PLACES_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("Google Places API key not found, using Nominatim");
            return geocodeRestaurant(restaurantName, city);
        }
        
        try {
            String query = restaurantName + ", " + (city != null ? city : "Gia Lâm") + ", Hanoi, Vietnam";
            String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
            
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + 
                        encodedQuery + "&key=" + apiKey;
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode json = objectMapper.readTree(response);
                JsonNode results = json.get("results");
                
                if (results != null && results.isArray() && results.size() > 0) {
                    JsonNode location = results.get(0).get("geometry").get("location");
                    double lat = location.get("lat").asDouble();
                    double lon = location.get("lng").asDouble();
                    
                    return new LocationCoordinate(restaurantName, lat, lon);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error with Google Places API: " + e.getMessage());
        }
        
        return null;
    }
}

