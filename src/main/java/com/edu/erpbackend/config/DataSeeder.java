package com.edu.erpbackend.config;

import com.edu.erpbackend.model.users.Role;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Check if the Super Admin already exists to avoid duplicates
        if (userRepository.findByEmail("admin@satyug.edu.in").isEmpty()) {

            User admin = new User();
            admin.setName("Super Admin");
            admin.setEmail("admin@satyug.edu.in"); // 👈 This is your Login Email
            admin.setPasswordHash(passwordEncoder.encode("admin123")); // 👈 This is your Login Password
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            System.out.println("✅ DEFAULT ADMIN CREATED: admin@satyug.edu.in / admin123");
        }
    }
}