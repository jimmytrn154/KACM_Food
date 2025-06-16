package com.Chese.KACM_Recommendation.Controller;

import com.Chese.KACM_Recommendation.Config.Restaurant;
import com.Chese.KACM_Recommendation.model.LocationCoordinate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    // Expose the static map of locations from your Restaurant.java file
    @GetMapping
    public Map<String, LocationCoordinate> getAllLocations() {
        return Restaurant.locations;
    }
}
