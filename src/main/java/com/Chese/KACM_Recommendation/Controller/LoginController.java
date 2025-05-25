package com.Chese.KACM_Recommendation.Controller;

import com.Chese.KACM_Recommendation.entities.Login;

import jakarta.servlet.http.HttpSession;

import com.Chese.KACM_Recommendation.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Autowired // @Autowired được sử dụng để tự động tiêm (dependency injection) một bean (ví dụ: LoginService) vào LoginController.
    // Mục đích: Cho phép controller sử dụng các phương thức của LoginService mà không cần tạo đối tượng thủ công.
    private LoginService loginService;

    @GetMapping("/login") // @GetMapping("/login") ánh xạ yêu cầu GET tới URL /login đến phương thức showLoginForm.
    // Mục đích: Hiển thị giao diện hoặc dữ liệu khi người dùng truy cập một URL (ví dụ: hiển thị form đăng nhập).
    public String showLoginForm(Model model) {
        // Model là một đối tượng trong Spring MVC, thường được sử dụng để lưu trữ dữ liệu mà bạn muốn gửi từ Controller đến View để hiển thị.
        model.addAttribute("login", new Login());
        return "login";
    }

    @PostMapping("/login") // @PostMapping("/login") ánh xạ yêu cầu POST tới URL /login đến phương thức processLogin.
    // Mục đích: Xử lý dữ liệu được gửi từ form (ví dụ: xử lý thông tin đăng nhập khi người dùng nhấn nút "Đăng nhập").
    public String processLogin(@ModelAttribute("login") Login login, Model model, HttpSession session) {
        // @ModelAttribute("login") Login login lấy dữ liệu từ form (tên người dùng, mật khẩu) và gán vào đối tượng login.
        // Mục đích: Giúp lấy dữ liệu người dùng nhập từ giao diện và sử dụng trong phương thức xử lý.
        if (loginService.authenticate(login.getUsername(), login.getPassword())) {
            // ← establish login
            session.setAttribute("user", login.getUsername());
            return "redirect:/restriction";
            // Trong Spring MVC, return "redirect:/welcome"; không trả về một View (như return "login";) mà thay vào đó yêu cầu Spring thực hiện một HTTP redirect (chuyển hướng) tới URL /welcome.
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}