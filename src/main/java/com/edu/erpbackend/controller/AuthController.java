package com.edu.erpbackend.controller;

import com.edu.erpbackend.dto.LoginRequest;
import com.edu.erpbackend.dto.RegisterRequest;
import com.edu.erpbackend.service.AuthService;
import com.edu.erpbackend.util.JwtUtil;
import com.edu.erpbackend.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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
}