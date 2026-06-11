package com.example.KodikaraGroupBusinessManagementApplication.DTO;

// This file is modified to match the screenshot response
public class LoginResponse {

    private boolean success;
    private String role;
    private String message;
    private String userId;

    public LoginResponse(boolean success, String role, String message) {
        this.success = success;
        this.role = role;
        this.message = message;
    }

    public LoginResponse(boolean success, String role, String message, String userId) {
        this.success = success;
        this.role = role;
        this.message = message;
        this.userId = userId;
    }

    // --- Getters and Setters ---

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}