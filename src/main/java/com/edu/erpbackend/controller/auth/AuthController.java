package com.edu.erpbackend.controller.auth;

import com.edu.erpbackend.dto.ForgotPasswordRequest;
import com.edu.erpbackend.dto.LoginRequest;
import com.edu.erpbackend.dto.LoginResponse;
import com.edu.erpbackend.dto.ResetPasswordRequest;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.Teacher;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Route 1: Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    // Route 2: Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.forgotPassword(request.getEmail());
        } catch (Exception e) {
            System.out.println("Forgot Password Error: " + e.getMessage());
        }
        return ResponseEntity.ok("If an account exists with that email, a password reset link has been sent.");
    }

    // Route 3: Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully. You can now login.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Route 4: Logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }

    // ✅ Route 5: Get Current Logged-In User (NEW)
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getMe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authService.getUserByEmail(email);

        String profileImageUrl = null;
        if (user instanceof Student s) {
            profileImageUrl = s.getProfileImageUrl();
        } else if (user instanceof Teacher t) {
            profileImageUrl = t.getProfileImageUrl();
        }

        return ResponseEntity.ok(LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .profileImageUrl(profileImageUrl)
                .build());
    }
}