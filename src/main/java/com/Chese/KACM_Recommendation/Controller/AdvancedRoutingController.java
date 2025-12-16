package com.Chese.KACM_Recommendation.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the advanced routing demo page
 */
@Controller
public class AdvancedRoutingController {
    
    @GetMapping("/advanced-routing")
    public String advancedRoutingPage() {
        return "advanced-routing";
    }
}

