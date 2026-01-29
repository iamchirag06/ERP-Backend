package com.edu.erpbackend.controller;

import com.edu.erpbackend.dto.ForgotPasswordRequest;
import com.edu.erpbackend.dto.LoginRequest;
import com.edu.erpbackend.dto.LoginResponse;
import com.edu.erpbackend.dto.ResetPasswordRequest;
import com.edu.erpbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ✅ Route 1: Login (Public)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
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

    // ✅ Route 4: Logout (Authenticated)
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Since we use JWT (stateless), the server just confirms the request.
        // The actual "logout" happens when the Frontend deletes the token.
        return ResponseEntity.ok("Logged out successfully");
    }
}