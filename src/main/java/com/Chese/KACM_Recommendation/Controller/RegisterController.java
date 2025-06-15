package com.Chese.KACM_Recommendation.Controller;
import com.Chese.KACM_Recommendation.entities.User;
import com.Chese.KACM_Recommendation.Service.LoginService;
// import com.Chese.KACM_Recommendation.entities.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.Chese.KACM_Recommendation.entities.Register;

@Controller
public class RegisterController {

    @Autowired
    private LoginService userService;  // or RegistrationService

    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("register", new Register());
        return "register";  // your Thymeleaf/JSP view
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("register") Register reg, Model model) {
        // turn your Register DTO into your domain User
        User newUser = new User(null,
                                reg.getUsername(),
                                reg.getEmail(),
                                reg.getPassword());
        userService.addUser(newUser);
        return "redirect:/login?registered";
    }   
}
