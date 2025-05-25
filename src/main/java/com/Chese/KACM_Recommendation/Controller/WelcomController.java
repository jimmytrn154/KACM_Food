package com.Chese.KACM_Recommendation.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class WelcomController {

    @GetMapping({"/", "/index"})
    public String indexPage(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
    }
    return "index"; // renders templates/index.html
  }
}
