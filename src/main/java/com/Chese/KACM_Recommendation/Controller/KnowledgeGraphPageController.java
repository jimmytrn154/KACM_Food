package com.Chese.KACM_Recommendation.Controller;

import com.Chese.KACM_Recommendation.Service.KnowledgeGraphService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for Knowledge Graph recommendation page
 */
@Controller
public class KnowledgeGraphPageController {
    
    @Autowired
    private KnowledgeGraphService kgService;
    
    @GetMapping("/kg-recommend")
    public String kgRecommendPage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("connected", kgService.isConnected());
        return "kg-recommend";
    }
}

