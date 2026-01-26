package com.edu.erpbackend.controller;

import com.edu.erpbackend.dto.ForgotPasswordRequest;
import com.edu.erpbackend.dto.LoginRequest;
import com.edu.erpbackend.dto.ResetPasswordRequest;
import com.edu.erpbackend.service.AuthService;
import com.edu.erpbackend.util.JwtUtil;
import com.edu.erpbackend.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // ✅ Route 1: Login (Public)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 1. Fetch user logic
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

        // 2. Check Password
        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }

        // 3. Generate Token
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // 4. Return Token
        return ResponseEntity.ok(token);
    }

    // ✅ Route 2: Forgot Password Request (Public)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.generateResetToken(request.getEmail());
        } catch (Exception e) {
            // Log for debugging, but hide error from user for security
            System.out.println("Forgot Password Error: " + e.getMessage());
        }

        return ResponseEntity.ok("If an account exists with that email, a password reset link has been sent.");
    }

    // ✅ Route 3: Submit New Password (Public)
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully. You can now login.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}