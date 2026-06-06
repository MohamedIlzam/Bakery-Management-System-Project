package com.example.KodikaraGroupBusinessManagementApplication.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword="owner";
        String encodedpassword=encoder.encode(rawPassword);

        System.out.println("--------------------");
        System.out.println("COPY THIS SQL COMMAND:");
        System.out.println("UPDATE user SET password='"+encodedpassword+"' WHERE username='owner';");
        System.out.println("--------------------");

    }
}
