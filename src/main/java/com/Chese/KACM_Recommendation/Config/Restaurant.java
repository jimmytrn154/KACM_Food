package com.Chese.KACM_Recommendation.Config;
import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import java.util.HashMap;
import java.util.Map;

public class Restaurant {
    public static final Map<String, LocationCoordinate> locations = new HashMap<>();
    static {
        locations.put("VinUniversity", new LocationCoordinate("VinUniversity", 20.98892, 105.94601));
        locations.put("Pho24", new LocationCoordinate("Pho24", 20.98970, 105.94350));
        locations.put("BunChaHaNoi", new LocationCoordinate("BunChaHaNoi", 20.98650, 105.94820));
        locations.put("ComTam", new LocationCoordinate("ComTam", 20.98730, 105.94410));
    }
}
