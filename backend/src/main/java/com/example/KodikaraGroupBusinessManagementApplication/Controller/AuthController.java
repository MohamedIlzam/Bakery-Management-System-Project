//package com.example.KodikaraGroupBusinessManagementApplication.Controller;
//
//import com.example.KodikaraGroupBusinessManagementApplication.DTO.LoginRequest;
//import com.example.KodikaraGroupBusinessManagementApplication.DTO.LoginResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            loginRequest.getUsername(),
//                            loginRequest.getPassword()
//                    )
//            );
//
//            // This sets the session for the user
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            // Get the role from the authentication object
//            String role = authentication.getAuthorities().stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .findFirst()
//                    .orElse("");
//
//            // Return the success response from your screenshot
//            return ResponseEntity.ok(new LoginResponse(true, role, "Login successful"));
//
//        } catch (AuthenticationException e) {
//            // Alternate Flow 1: Login details incorrect
//            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
//        }
//    }
//}
package com.example.KodikaraGroupBusinessManagementApplication.Controller;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.LoginRequest;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.LoginResponse;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.MessageResponse;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.UserDTO;
import com.example.KodikaraGroupBusinessManagementApplication.model.User;
import com.example.KodikaraGroupBusinessManagementApplication.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    //The Repository that handles session saving
    private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/login")
    //HttpServletRequest and HttpServletResponse to the method arguments
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Fetch user entity to verify approval status
            User user = userService.getUserByUsername(loginRequest.getUsername());
            if (!user.isApproved()) {
                return new ResponseEntity<>("Your account is pending approval by the Owner.", HttpStatus.FORBIDDEN);
            }

            // Create a new Security Context
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);


            securityContextRepository.saveContext(context, request, response);

            // Get the role from the authentication object
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("");

            return ResponseEntity.ok(new LoginResponse(true, role, "Login successful"));

        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        User createdUser = userService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Account created for " + createdUser.getUsername() + " (pending owner approval)"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        String usernameOrEmail = request.get("usernameOrEmail");
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username or recovery email is required"));
        }
        try {
            userService.sendVerificationCode(usernameOrEmail);
            return ResponseEntity.ok(new MessageResponse("Verification code sent successfully. Check your email."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody java.util.Map<String, String> request) {
        String usernameOrEmail = request.get("usernameOrEmail");
        String code = request.get("code");
        if (usernameOrEmail == null || code == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username/email and verification code are required"));
        }
        boolean isValid = userService.verifyCode(usernameOrEmail, code);
        if (isValid) {
            return ResponseEntity.ok(new MessageResponse("Verification code is valid"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Invalid or expired verification code"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody java.util.Map<String, String> request) {
        String usernameOrEmail = request.get("usernameOrEmail");
        String code = request.get("code");
        String newPassword = request.get("newPassword");
        if (usernameOrEmail == null || code == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid request data. Password must be at least 6 characters."));
        }
        try {
            userService.resetPassword(usernameOrEmail, code, newPassword);
            return ResponseEntity.ok(new MessageResponse("Password has been reset successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        }
    }
}







































































































































































































































































































































































































































































































