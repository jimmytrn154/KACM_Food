package com.Chese.KACM_Recommendation.Controller;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.Chese.KACM_Recommendation.Service.*;
import com.Chese.KACM_Recommendation.entities.*;
import com.Chese.KACM_Recommendation.model.FoodSummary;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Controller
public class RecommendController {
    @Autowired
    private RecommendationService svc;
    @GetMapping("/recommend")
    public String recommendPage(HttpSession session){
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
          }
        return "recommend";
    }

    
    @GetMapping("/diet")
    @ResponseBody
    public List<FoodSummary> recommendByDiet(@SessionAttribute("restriction") Restriction restriction) {
        return svc.getRecommendationsByDiet(restriction);
    }

    @GetMapping ("/preferred_cuisines")
    @ResponseBody
    public List<FoodSummary> recommendByCuisine(@SessionAttribute("restriction") Restriction restriction) {
        return svc.getRecommendationsByCusine(restriction);
    }
}
