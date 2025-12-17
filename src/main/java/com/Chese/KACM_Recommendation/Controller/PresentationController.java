package com.Chese.KACM_Recommendation.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for presentation page
 */
@Controller
public class PresentationController {
    
    @GetMapping("/presentation")
    public String presentationPage() {
        return "presentation";
    }
}

