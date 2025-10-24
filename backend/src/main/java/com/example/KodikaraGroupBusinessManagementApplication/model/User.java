package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="user")
public class User {
    @Id
    @Column(name = "user_id", columnDefinition = "CHAR(10)")
    private String userId;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;
//
//    @Column(name = "email", length = 100, nullable = false, unique = true)
//    private String email;

//    @Column(name = "full_name", length = 100, nullable = false)
//    private String fullName;

    @Column(name = "role", length = 20, nullable = false)
    private String role;

//    @Column(name = "created_at")
//    private LocalDateTime createdAt;

//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
    //}
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

//    public void setEmail(String email) {
//        this.email = email;
//    }

//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }

    public void setRole(String role) {
        this.role = role;
    }
}





//CREATE TABLE user(
//        user_id CHAR(15) NOT NULL,
//username VARCHAR(30) NOT NULL UNIQUE,
//password VARCHAR(25) NOT NULL UNIQUE,
//role VARCHAR(12) CHECK(role IN('ADMIN','DATAENTRY','DEVELOPER')) ,
//PRIMARY KEY (user_id)
//);