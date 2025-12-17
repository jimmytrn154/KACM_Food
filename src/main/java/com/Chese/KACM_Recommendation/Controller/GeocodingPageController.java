package com.Chese.KACM_Recommendation.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for geocoding page
 */
@Controller
public class GeocodingPageController {
    
    @GetMapping("/geocode")
    public String geocodePage() {
        return "geocode-restaurants";
    }
}

