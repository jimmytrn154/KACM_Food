package com.Chese.KACM_Recommendation.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.Chese.KACM_Recommendation.entities.User;

@Service
// LoginService là một service bean.
// Spring sẽ tự động tạo một instance của LoginService và quản lý nó.
// Các lớp khác (như LoginController) có thể sử dụng LoginService thông qua dependency injection (ví dụ: thông qua @Autowired).
public class LoginService {
    private List<User> users = new ArrayList<>();

    public LoginService() {
        // Tích hợp data mẫu
        users.add(new User(1L, "Nguyen Van Duy Anh", "duyanh@example.com", "bachaphomai"));
        users.add(new User(2L, "Tran Anh Chuong", "anhchuong@example.com", "phuonganh"));
        users.add(new User(3L, "Duong Hien Chi Kien", "chikien@example.com", "chikien"));
    }

    public boolean authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public void addUser(User user) {
        users.add(user);
    }
}

// Trong đoạn code của bạn, LoginService được sử dụng trong LoginController thông qua annotation @Autowired. 
// Điều này có nghĩa là Spring Framework sẽ tự động "tiêm" (inject) một instance của LoginService vào LoginController 
// mà bạn không cần phải tạo hoặc gán thủ công.