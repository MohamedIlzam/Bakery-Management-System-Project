package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="user")
public class User {
    @Id
    @Column(name = "user_id", columnDefinition = "CHAR(10)")
    private String userId;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "role", length = 20, nullable = false)
    private String role;

    @Column(name = "recovery_email", length = 100, unique = true)
    private String recoveryEmail;

    @Column(name = "verification_code", length = 6)
    private String verificationCode;

    @Column(name = "code_expiry_time")
    private java.time.LocalDateTime codeExpiryTime;

    @Column(name = "approved", nullable = false)
    private boolean approved = true;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRecoveryEmail() {
        return recoveryEmail;
    }

    public void setRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public java.time.LocalDateTime getCodeExpiryTime() {
        return codeExpiryTime;
    }

    public void setCodeExpiryTime(java.time.LocalDateTime codeExpiryTime) {
        this.codeExpiryTime = codeExpiryTime;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}