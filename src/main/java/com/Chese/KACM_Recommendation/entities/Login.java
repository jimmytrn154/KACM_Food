package com.Chese.KACM_Recommendation.entities;

public class Login {
    private String username;
    private String password;

    public Login() {}
    // Login là một Java Bean (hoặc POJO - Plain Old Java Object) 
    // được sử dụng để đại diện cho dữ liệu của một biểu mẫu đăng nhập, với hai thuộc tính username và password.
    
    // public Login(String username, String password) {
    //     this.username = username;
    //     this.password = password;
    // }

    public String getUsername() {
        return username;
        // Cho phép các lớp khác (như LoginService hoặc View) truy cập giá trị của username một cách an toàn.
    }

    public void setUsername(String username) {
        this.username = username;
        // Cho phép các lớp khác hoặc framework cập nhật giá trị của username.
        // Binding dữ liệu form: Khi người dùng gửi form đăng nhập, Spring MVC sử dụng setter để gán giá trị từ form (như tên người dùng nhập vào) vào đối tượng Login.
        // Ví dụ, trong processLogin, @ModelAttribute("login") Login login dựa vào setUsername để cập nhật giá trị username.
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        // Binding dữ liệu từ form (Spring MVC gọi setPassword để gán mật khẩu từ form vào đối tượng Login).
    }
}