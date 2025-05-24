package com.Chese.KACM_Recommendation.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomController {

    @GetMapping("/welcome")
    public String welcomePage(){
        return "welcome";
    }
}
