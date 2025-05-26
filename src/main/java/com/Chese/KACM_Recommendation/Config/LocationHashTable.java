package com.Chese.KACM_Recommendation.Config;

import java.util.HashMap;
import java.util.Map;

public class LocationHashTable {
    public static Map<String, String> userLocations = new HashMap<>();
    public static Map<String, String> restaurantLocations = new HashMap<>();

    static {
        // Users
        userLocations.put("user1", "VinUniversity");
        userLocations.put("user2", "Vinhomes Ocean Park");

        // Restaurants
        restaurantLocations.put("Pho24", "Pho 24 - Đa Tốn");
        restaurantLocations.put("BunChaHaNoi", "Bún Chả Hà Nội - Gia Lâm");
        restaurantLocations.put("ComTam", "Cơm Tấm Sài Gòn - Đa Tốn");
    }
}
