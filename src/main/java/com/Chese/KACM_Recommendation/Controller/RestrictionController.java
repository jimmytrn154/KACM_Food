package com.Chese.KACM_Recommendation.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import com.Chese.KACM_Recommendation.entities.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class RestrictionController {
    @GetMapping("/restriction")
    public String restrictionPage(HttpSession session, Model model){
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
    }
        model.addAttribute("restriction", new Restriction());
        return "restriction";
    }

    @PostMapping("/restriction/dislikefl")
    public String addRestriction(HttpSession session,
                                @ModelAttribute("restriction") Restriction restriction) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
    }
        System.out.println(restriction.gethate_taste_adjectives());
        System.out.println(restriction.getfood_allergy());
        System.out.println(restriction.getdiet());
        System.out.println(restriction.getPreferred_Cuisines());
        session.setAttribute("restriction", restriction);
        return "redirect:/index";
    }
}
