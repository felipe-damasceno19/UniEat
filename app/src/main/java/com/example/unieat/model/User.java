package com.example.unieat.model;

import com.example.unieat.enums.UserType;

public class User {

    private String name;

    private String username;

    private String email;

    private String password;

    private Double balance;

    private UserType type;

    public User() {}

    public User(String name, String username, String password, String email, Double balance, UserType type) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.balance = balance;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Double getBalance() {
        return balance;
    }

    public UserType getType() {
        return type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
    
    public void setType(UserType type) {
        this.type = type;
    }
}
