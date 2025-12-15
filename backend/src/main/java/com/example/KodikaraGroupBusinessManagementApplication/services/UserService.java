package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.UserDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.UserUpdateDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.UserRepository;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.User;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // *** ADD THIS METHOD - Required by UserDetailsService ***
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Check if this method is being called repeatedly
        System.out.println(">>> DEBUG: Entering loadUserByUsername for: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Check if the database retrieval worked
        System.out.println(">>> DEBUG: User found in DB: " + user.getUsername());

        String role = user.getRole();
        if (role == null || role.trim().isEmpty()) {
            role = "ROLE_SALESMAN";
        }

        // 3. Check if we reach the end of the method
        System.out.println(">>> DEBUG: returning UserDetails object...");

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .build();
    }

    // (Create Salesman)
    public User createSalesman(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already registered");
        }
        User newUser = new User();
        newUser.setUserId(IdGenerator.userId());
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRole(userDTO.getRole() != null ? userDTO.getRole() : "ROLE_SALESMAN"); // Use provided role or default
        return userRepository.save(newUser);
    }

    // Get All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get User By Role(For list down salesman)
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    //  Get One User by ID
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    // Update User Details
    public User updateUser(String userId, UserUpdateDTO userUpdateDTO) {
        User userToUpdate = getUserById(userId);

        Optional<User> userWithNewUsername = userRepository.findByUsername(userUpdateDTO.getUsername());
        if (userWithNewUsername.isPresent() && !userWithNewUsername.get().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Username is already taken");
        }

        userToUpdate.setUsername(userUpdateDTO.getUsername());

        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            userToUpdate.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        // Update role if provided
        if (userUpdateDTO.getRole() != null && !userUpdateDTO.getRole().isEmpty()) {
            userToUpdate.setRole(userUpdateDTO.getRole());
        }

        return userRepository.save(userToUpdate);
    }

    //  Delete a User
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
}