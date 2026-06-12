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
    private final org.springframework.mail.javamail.JavaMailSender mailSender;

    @Autowired
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, @Autowired(required = false) org.springframework.mail.javamail.JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
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
        } else {
            // Map new clean role strings to Spring Security's expected authority strings
            if ("Salesman".equalsIgnoreCase(role)) {
                role = "ROLE_SALESMAN";
            } else if ("Owner".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role)) {
                role = "ROLE_OWNER";
            } else if ("Driver".equalsIgnoreCase(role)) {
                role = "ROLE_DRIVER";
            }
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
        if (userDTO.getPassword() == null || userDTO.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already registered");
        }
        User newUser = new User();
        newUser.setUserId(IdGenerator.userId());
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRecoveryEmail(userDTO.getRecoveryEmail());
        newUser.setApproved(true); // Owner created users are pre-approved
        
        // Map the role back to DB/Spring Security compatible values
        String role = userDTO.getRole();
        if (role == null || role.trim().isEmpty()) {
            role = "ROLE_SALESMAN";
        } else {
            if ("Salesman".equalsIgnoreCase(role)) {
                role = "ROLE_SALESMAN";
            } else if ("Owner".equalsIgnoreCase(role)) {
                role = "ROLE_OWNER";
            } else if ("Driver".equalsIgnoreCase(role)) {
                role = "ROLE_DRIVER";
            }
        }
        newUser.setRole(role);
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

    // Get One User by ID
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
            if (userUpdateDTO.getPassword().length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters long");
            }
            userToUpdate.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        // Update role if provided
        if (userUpdateDTO.getRole() != null && !userUpdateDTO.getRole().isEmpty()) {
            String role = userUpdateDTO.getRole();
            if ("Salesman".equalsIgnoreCase(role)) {
                role = "ROLE_SALESMAN";
            } else if ("Owner".equalsIgnoreCase(role)) {
                role = "ROLE_OWNER";
            } else if ("Driver".equalsIgnoreCase(role)) {
                role = "ROLE_DRIVER";
            }
            userToUpdate.setRole(role);
        }

        return userRepository.save(userToUpdate);
    }

    // Delete a User
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    public void sendVerificationCode(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrRecoveryEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username or email: " + usernameOrEmail));

        // Generate 6 digit code
        String code = String.format("%06d", new java.util.Random().nextInt(999999));
        user.setVerificationCode(code);
        user.setCodeExpiryTime(java.time.LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // Print code to console for easy local verification
        System.out.println("=================================================");
        System.out.println("VERIFICATION CODE FOR " + user.getUsername() + ": " + code);
        System.out.println("=================================================");

        // Send email if mailSender is available and email is present
        if (mailSender != null && user.getRecoveryEmail() != null && !user.getRecoveryEmail().isEmpty()) {
            try {
                org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
                message.setTo(user.getRecoveryEmail());
                message.setSubject("Password Verification Code");
                message.setText("Your verification code is: " + code + "\nIt will expire in 15 minutes.");
                mailSender.send(message);
            } catch (Exception e) {
                System.out.println("Failed to send verification email: " + e.getMessage());
            }
        }
    }

    public boolean verifyCode(String usernameOrEmail, String code) {
        User user = userRepository.findByUsernameOrRecoveryEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username or email: " + usernameOrEmail));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            return false;
        }

        if (user.getCodeExpiryTime() == null || user.getCodeExpiryTime().isBefore(java.time.LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    public void resetPassword(String usernameOrEmail, String code, String newPassword) {
        User user = userRepository.findByUsernameOrRecoveryEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username or email: " + usernameOrEmail));

        if (!verifyCode(usernameOrEmail, code)) {
            throw new IllegalArgumentException("Invalid or expired verification code");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerificationCode(null);
        user.setCodeExpiryTime(null);
        userRepository.save(user);
    }

    public User registerUser(UserDTO userDTO) {
        if (userDTO.getPassword() == null || userDTO.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already registered");
        }
        User newUser = new User();
        newUser.setUserId(IdGenerator.userId());
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRecoveryEmail(userDTO.getRecoveryEmail());

        String role = userDTO.getRole();
        if (role == null || role.trim().isEmpty()) {
            role = "ROLE_SALESMAN";
        } else {
            if ("Salesman".equalsIgnoreCase(role)) {
                role = "ROLE_SALESMAN";
            } else if ("Owner".equalsIgnoreCase(role)) {
                role = "ROLE_OWNER";
            } else if ("Driver".equalsIgnoreCase(role)) {
                role = "ROLE_DRIVER";
            }
        }
        newUser.setRole(role);
        
        // Auto-approve owners ONLY if there are no active owners in the database
        if ("ROLE_OWNER".equals(role)) {
            long activeOwnerCount = userRepository.countByRoleAndApproved("ROLE_OWNER", true);
            if (activeOwnerCount == 0) {
                newUser.setApproved(true);
            } else {
                newUser.setApproved(false);
            }
        } else {
            newUser.setApproved(false);
        }
        
        return userRepository.save(newUser);
    }

    public List<User> getPendingUsers() {
        return userRepository.findByApproved(false);
    }

    public void approveUser(String userId) {
        User user = getUserById(userId);
        user.setApproved(true);
        userRepository.save(user);
    }

    public void rejectUser(String userId) {
        deleteUser(userId);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    public void sendEmailUpdateCode(String username, String newEmail) {
        User user = getUserByUsername(username);

        if (user.getRecoveryEmail() != null && !user.getRecoveryEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Recovery email is already configured. Please remove it first.");
        }

        // Generate 6 digit code
        String code = String.format("%06d", new java.util.Random().nextInt(999999));
        user.setVerificationCode(code);
        user.setCodeExpiryTime(java.time.LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // Print code to console for easy local verification
        System.out.println("=================================================");
        System.out.println("EMAIL UPDATE VERIFICATION CODE FOR " + user.getUsername() + ": " + code);
        System.out.println("=================================================");

        // Send email if mailSender is available
        if (mailSender != null) {
            try {
                org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
                message.setTo(newEmail);
                message.setSubject("Email Update Verification Code");
                message.setText("Your verification code is: " + code + "\nIt will expire in 15 minutes.");
                mailSender.send(message);
            } catch (Exception e) {
                System.out.println("Failed to send email update verification email: " + e.getMessage());
            }
        }
    }

    public void updateRecoveryEmail(String username, String newEmail, String code) {
        User user = getUserByUsername(username);

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        if (user.getCodeExpiryTime() == null || user.getCodeExpiryTime().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code has expired");
        }

        user.setRecoveryEmail(newEmail);
        user.setVerificationCode(null);
        user.setCodeExpiryTime(null);
        userRepository.save(user);
    }

    public void sendEmailRemoveCode(String username) {
        User user = getUserByUsername(username);
        String existingEmail = user.getRecoveryEmail();
        if (existingEmail == null || existingEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("No recovery email configured to remove.");
        }

        // Generate 6 digit code
        String code = String.format("%06d", new java.util.Random().nextInt(999999));
        user.setVerificationCode(code);
        user.setCodeExpiryTime(java.time.LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // Print code to console for easy local verification
        System.out.println("=================================================");
        System.out.println("EMAIL REMOVAL VERIFICATION CODE FOR " + user.getUsername() + ": " + code);
        System.out.println("=================================================");

        // Send email if mailSender is available
        if (mailSender != null) {
            try {
                org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
                message.setTo(existingEmail);
                message.setSubject("Email Removal Verification Code");
                message.setText("Your verification code is: " + code + "\nIt will expire in 15 minutes.");
                mailSender.send(message);
            } catch (Exception e) {
                System.out.println("Failed to send email removal verification email: " + e.getMessage());
            }
        }
    }

    public void removeRecoveryEmail(String username, String code) {
        User user = getUserByUsername(username);

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        if (user.getCodeExpiryTime() == null || user.getCodeExpiryTime().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code has expired");
        }

        user.setRecoveryEmail(null);
        user.setVerificationCode(null);
        user.setCodeExpiryTime(null);
        userRepository.save(user);
    }

    public User updateProfile(String currentUsername, UserUpdateDTO profileDTO) {
        User user = getUserByUsername(currentUsername);

        // If username is changing, check if new username already exists
        if (profileDTO.getUsername() != null && !profileDTO.getUsername().equals(currentUsername)) {
            if (userRepository.existsByUsername(profileDTO.getUsername())) {
                throw new IllegalArgumentException("Username is already taken");
            }
            user.setUsername(profileDTO.getUsername());
        }

        if (profileDTO.getPassword() != null && !profileDTO.getPassword().trim().isEmpty()) {
            if (profileDTO.getPassword().length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters long");
            }
            user.setPassword(passwordEncoder.encode(profileDTO.getPassword()));
        }

        return userRepository.save(user);
    }
}