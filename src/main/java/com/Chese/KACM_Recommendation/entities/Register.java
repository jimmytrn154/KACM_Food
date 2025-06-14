// src/main/java/com/Chese/KACM_Recommendation/entities/Register.java
package com.Chese.KACM_Recommendation.entities;

public class Register {
    private String username;
    private String password;
    private String email;
    private Integer age;

    public Register() {
    }

    public Register(String username, String password, String email, Integer age) {
        this.username = username;
        this.password = password;
        this.email    = email;
        this.age      = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
