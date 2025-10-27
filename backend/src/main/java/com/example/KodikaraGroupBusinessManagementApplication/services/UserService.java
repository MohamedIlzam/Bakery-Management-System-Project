package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.UserDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.UserUpdateDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.UserRepository;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.User;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- C (Create Salesman) - Keep specific for now ---
    public User createSalesman(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already registered");
        }
        User newUser = new User();
        newUser.setUserId(IdGenerator.userId());
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRole("ROLE_SALESMAN"); // Explicitly set role
        return userRepository.save(newUser);
    }

    // --- R (Read) - Get All Users ---
    public List<User> getAllUsers() { // Renamed
        return userRepository.findAll();
    }

    // --- R (Read) - Get One User by ID ---
    public User getUserById(String userId) { // Renamed
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    // --- U (Update) - Update User Details ---
    public User updateUser(String userId, UserUpdateDTO userUpdateDTO) { // Renamed
        User userToUpdate = getUserById(userId);

        Optional<User> userWithNewUsername = userRepository.findByUsername(userUpdateDTO.getUsername());
        if (userWithNewUsername.isPresent() && !userWithNewUsername.get().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Username is already taken");
        }

        userToUpdate.setUsername(userUpdateDTO.getUsername());

        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            userToUpdate.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        return userRepository.save(userToUpdate);
    }

    // --- D (Delete) - Delete a User ---
    public void deleteUser(String userId) { // Renamed
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
}