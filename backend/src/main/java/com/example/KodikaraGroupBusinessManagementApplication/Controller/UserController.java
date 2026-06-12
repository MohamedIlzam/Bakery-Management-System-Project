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
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;


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

    // Create User
    @PostMapping("/create")
    public ResponseEntity<User> createSalesman(@Valid @RequestBody UserDTO userDTO) {
        User createdSalesman = userService.createSalesman(userDTO);
        return new ResponseEntity<>(createdSalesman, HttpStatus.CREATED);
    }

    // Get Pending Approvals
    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingUsers() {
        List<User> pending = userService.getPendingUsers();
        return new ResponseEntity<>(pending, HttpStatus.OK);
    }

    // Approve User
    @PostMapping("/pending/{id}/approve")
    public ResponseEntity<MessageResponse> approveUser(@PathVariable String id) {
        userService.approveUser(id);
        return ResponseEntity.ok(new MessageResponse("User approved successfully"));
    }

    // Reject User
    @PostMapping("/pending/{id}/reject")
    public ResponseEntity<MessageResponse> rejectUser(@PathVariable String id) {
        userService.rejectUser(id);
        return ResponseEntity.ok(new MessageResponse("User rejected/deleted successfully"));
    }

    // Get All Users
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllSalesmen() { // Renamed from getAllSalesmen


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


        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Get Logged-in User Profile
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userService.getUserByUsername(currentUsername);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Update Logged-in User Profile
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@Valid @RequestBody UserUpdateDTO profileDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User updatedUser = userService.updateProfile(currentUsername, profileDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // Send Verification Code for Email Update
    @PostMapping("/profile/email/send-code")
    public ResponseEntity<MessageResponse> sendEmailUpdateCode(@RequestBody java.util.Map<String, String> request) {
        String newEmail = request.get("email");
        if (newEmail == null || newEmail.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email is required"));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        try {
            userService.sendEmailUpdateCode(currentUsername, newEmail);
            return ResponseEntity.ok(new MessageResponse("Verification code sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        }
    }

    // Verify Code and Update Recovery Email
    @PostMapping("/profile/email/verify-code")
    public ResponseEntity<MessageResponse> verifyEmailUpdateCode(@RequestBody java.util.Map<String, String> request) {
        String newEmail = request.get("email");
        String code = request.get("code");
        if (newEmail == null || code == null || newEmail.trim().isEmpty() || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email and code are required"));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        try {
            userService.updateRecoveryEmail(currentUsername, newEmail, code);
            return ResponseEntity.ok(new MessageResponse("Recovery email updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        }
    }

    // Send Verification Code for Email Removal
    @PostMapping("/profile/email/send-remove-code")
    public ResponseEntity<MessageResponse> sendEmailRemoveCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        try {
            userService.sendEmailRemoveCode(currentUsername);
            return ResponseEntity.ok(new MessageResponse("Verification code sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        }
    }

    // Verify Code and Remove Recovery Email
    @PostMapping("/profile/email/remove")
    public ResponseEntity<MessageResponse> removeRecoveryEmail(@RequestBody java.util.Map<String, String> request) {
        String code = request.get("code");
        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Verification code is required"));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        try {
            userService.removeRecoveryEmail(currentUsername, code);
            return ResponseEntity.ok(new MessageResponse("Recovery email removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        }
    }

    // Get Users For Dropdown List
    @GetMapping("/lookup")
    public ResponseEntity<List<User>> getSalesmanLookup(){
        List<User> salesman = userService.getUsersByRole("DATAENTRY");
        return new ResponseEntity<>(salesman, HttpStatus.OK);
    }

    // Get One User by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getSalesmanById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Update a User
    @PutMapping("/{id}")
    public ResponseEntity<User> updateSalesman(@PathVariable String id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        User updatedUser = userService.updateUser(id, userUpdateDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // Delete a User
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesman(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}