package com.Chese.KACM_Recommendation.entities;

public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    User(){

    }
    public User(Long id, String username, String email, String password){
        this.id=id;
        this.username=username;
        this.email=email;
        this.password=password;
    }
    public void setId(Long id){
        this.id=id;
    }
    public Long getId(){
        return this.id;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getEmail(){
        return this.email;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public String getPassword(){
        return this.password;
    }
}
