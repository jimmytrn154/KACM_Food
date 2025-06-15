package com.Chese.KACM_Recommendation.Controller;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.Chese.KACM_Recommendation.Service.*;
import com.Chese.KACM_Recommendation.entities.*;
import com.Chese.KACM_Recommendation.model.FoodSummary;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Controller
public class RecommendController {

    private int state;
    @Autowired
    private RecommendationService svc;

    RecommendController(){
        this.state=0;
    }

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
        this.state=0;
        return svc.getRecommendationsByDiet(restriction);
    }

    @GetMapping ("/preferred_cuisines")
    @ResponseBody
    public List<FoodSummary> recommendByCuisine(@SessionAttribute("restriction") Restriction restriction) {
        this.state=1;
        return svc.getRecommendationsByCusine(restriction);
    }

    @GetMapping("/delete/{id}")
    @ResponseBody
    public void delete(@PathVariable String id){
        svc.delete(id);
        return ;
    }

    @GetMapping("/restaurant_recommend")
    @ResponseBody
    public List<FoodSummary> getfood(@SessionAttribute("restriction") Restriction restriction){
        if(state==0){
            return svc.getRecommendationsByDiet(restriction);
        } else {
            return svc.getRecommendationsByCusine(restriction);
        }
    }

}
