package com.example.KodikaraGroupBusinessManagementApplication.Controller;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.MessageResponse;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.UserDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.UserUpdateDTO;
import com.example.KodikaraGroupBusinessManagementApplication.model.User;
import com.example.KodikaraGroupBusinessManagementApplication.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// REMOVE: import org.springframework.security.access.prepost.PreAuthorize; // Keep removed
import org.springframework.web.bind.annotation.*;
// --- Logging Imports ---
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
// --- End Logging Imports ---

import java.util.List;

@RestController
@RequestMapping("/api/salesman")
public class UserController {

    // Add Logger
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- C (Create) ---
    @PostMapping("/create")
    public ResponseEntity<User> createSalesman(@Valid @RequestBody UserDTO userDTO) {
        User createdSalesman = userService.createSalesman(userDTO);
        return new ResponseEntity<>(createdSalesman, HttpStatus.CREATED);
    }

    // --- R (Read) - Get All Users ---
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllSalesmen() { // Renamed from getAllSalesmen

        // --- START: ADD LOGGING ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            log.info("User {} is authenticated.", authentication.getName());
            log.info("Authorities:");
            if (authentication.getAuthorities() != null) {
                for (GrantedAuthority authority : authentication.getAuthorities()) {
                    log.info("- {}", authority.getAuthority());
                }
            } else {
                log.warn("Authentication authorities are null!");
            }
        } else {
            log.warn("User is Anonymous or NOT authenticated!");
        }
        // --- END: ADD LOGGING ---

        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // --- R (Read) - Get One User by ID ---
    @GetMapping("/{id}")
    public ResponseEntity<User> getSalesmanById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // --- U (Update) - Update a User ---
    @PutMapping("/{id}")
    public ResponseEntity<User> updateSalesman(@PathVariable String id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        User updatedUser = userService.updateUser(id, userUpdateDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // --- D (Delete) - Delete a User ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesman(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}