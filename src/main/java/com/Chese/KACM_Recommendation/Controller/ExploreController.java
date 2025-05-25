package com.Chese.KACM_Recommendation.Controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExploreController {

  @GetMapping("/explore")
  public String explorePage(HttpSession session) {
    if (session.getAttribute("user") == null) {
      return "redirect:/login";
    }
    return "explore"; // renders templates/explore.html
  }
}
