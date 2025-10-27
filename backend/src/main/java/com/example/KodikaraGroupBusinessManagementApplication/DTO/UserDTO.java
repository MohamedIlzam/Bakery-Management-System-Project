package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// This DTO captures the "Owner inputs the details of salesman"
public class UserDTO {

    @NotBlank(message = "Username is required") // Handles "Alternate Flow 1: Required fields are empty"
    @Size(min = 3, message = "Username must be at least 3 characters long")
    private String username;

    @NotBlank(message = "Password is required") // Handles "Alternate Flow 1: Required fields are empty"
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