package com.example.KodikaraGroupBusinessManagementApplication.config;

import com.example.KodikaraGroupBusinessManagementApplication.Repo.UserRepository;
import com.example.KodikaraGroupBusinessManagementApplication.model.User;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create the "owner" user if they don't exist
        if (!userRepository.existsByUsername("owner")) {
            User owner = new User();
            owner.setUserId(IdGenerator.userId());
            owner.setUsername("owner");
            owner.setPassword(passwordEncoder.encode("owner123")); // Set a default password
            owner.setRole("ROLE_OWNER");
            userRepository.save(owner);
            System.out.println("--- CREATED DEFAULT OWNER USER: username='owner', password='owner123' ---");
        }
    }
}