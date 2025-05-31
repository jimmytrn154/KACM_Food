package com.Chese.KACM_Recommendation.Controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecommendController {
    @GetMapping("/recommend")
    public String recommendPage(HttpSession session){
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
          }
        return "recommend";
    }
}
