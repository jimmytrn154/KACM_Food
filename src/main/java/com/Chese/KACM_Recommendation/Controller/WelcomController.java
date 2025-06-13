package com.Chese.KACM_Recommendation.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class WelcomController {

    /**
     * Handles requests to the root URL ("/").
     * This is the public landing page.
     */
    @GetMapping("/")
    public String showLandingPage() {
        return "landingPage"; // Renders templates/landingPage.html
    }

    /**
     * Handles requests to the protected "/index" URL.
     * Redirects to /login if the user is not authenticated.
     */
    @GetMapping("/index")
    public String showIndexPage(HttpSession session) {
        if (session.getAttribute("user") == null) {
            // This redirect correctly points to the route handled by your LoginController
            return "redirect:/login";
        }
        return "index"; // Renders templates/index.html for logged-in users
    }
}
