package com.Chese.KACM_Recommendation.Config;
import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import java.util.HashMap;
import java.util.Map;

public class Restaurant {
    public static final Map<String, LocationCoordinate> locations = new HashMap<>();
    static {
        locations.put("VinUniversity", new LocationCoordinate("VinUniversity", 20.98892, 105.94601));
        locations.put("f1", new LocationCoordinate("Pho24", 20.98970, 105.94350));
        locations.put("f2", new LocationCoordinate("BunChaHaNoi", 20.98650, 105.94820));
        locations.put("f3", new LocationCoordinate("Oliver’s Pizza", 20.99153399887994, 105.94365021615093));
        locations.put("f4", new LocationCoordinate("Le Champ", 21.010339982593003, 105.93975935296719));
        locations.put("f5", new LocationCoordinate("BunChaHaNoi", 20.98650, 105.94820));
        locations.put("f6", new LocationCoordinate("Phở Bò Gia Truyền", 20.99118084335844, 105.93982488377648));
        locations.put("f7", new LocationCoordinate("Pho24", 20.98970, 105.94350));
        locations.put("f8", new LocationCoordinate("BunChaHaNoi", 20.98650, 105.94820));
        locations.put("f9", new LocationCoordinate("ComTam", 20.98730, 105.94410));
        locations.put("f10", new LocationCoordinate("Pho24", 20.98970, 105.94350));
        locations.put("f11", new LocationCoordinate("BunChaHaNoi", 20.98650, 105.94820));
        locations.put("f12", new LocationCoordinate("Le Champ", 21.010339982593003, 105.93975935296719));
        locations.put("f13", new LocationCoordinate("Pho24", 20.98970, 105.94350));
        locations.put("f14", new LocationCoordinate("Le Champ", 21.010339982593003, 105.93975935296719));
        locations.put("f15", new LocationCoordinate("Le Champ", 21.010339982593003, 105.93975935296719));
        locations.put("f16", new LocationCoordinate("Chicken Rice", 21.0021197858423, 105.94016527989241));
        locations.put("f17", new LocationCoordinate("Sammy Restaurant Vinhomes Ocean Park", 20.992154164941173, 105.9558176664607));
        locations.put("f18", new LocationCoordinate("Sammy Restaurant Vinhomes Ocean Park", 20.992154164941173, 105.9558176664607));
        locations.put("f19", new LocationCoordinate("Pho24", 20.98970, 105.94350));
        locations.put("f20", new LocationCoordinate("Oliver’s Pizza", 20.99153399887994, 105.94365021615093));
    }
}
    