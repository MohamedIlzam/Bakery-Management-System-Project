package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, message = "Username must be at least 3 characters long")
    private String username;

    // Password can be optional during an update
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    // --- Getters and Setters ---
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
}