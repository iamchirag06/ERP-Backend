package com.edu.erpbackend.service;

import com.edu.erpbackend.dto.LoginRequest;
import com.edu.erpbackend.dto.LoginResponse;
import com.edu.erpbackend.dto.RegisterRequest;
import com.edu.erpbackend.model.*;
import com.edu.erpbackend.repository.*;
import com.edu.erpbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final BranchRepository branchRepository;

    // ✅ NEW: Needed for Login
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        // 1. Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Fetch User
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Generate Token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // 4. Return Response DTO
        return LoginResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .build();
    }

    public void register(RegisterRequest request) {
        // 1. Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // 2. Handle Logic based on Role
        if (request.getRole() == Role.STUDENT) {
            Student student = new Student();
            student.setEmail(request.getEmail());
            student.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            student.setRole(Role.STUDENT);
            student.setName(request.getName());

            student.setRollNo(request.getRollNo());
            student.setSemester(request.getSemester());

            if (request.getBranchId() != null) {
                Branch branch = branchRepository.findById(request.getBranchId())
                        .orElseThrow(() -> new RuntimeException("Branch not found"));
                student.setBranch(branch);
            }
            studentRepository.save(student);

        } else if (request.getRole() == Role.TEACHER) {
            Teacher teacher = new Teacher();
            teacher.setEmail(request.getEmail());
            teacher.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            teacher.setRole(Role.TEACHER);
            teacher.setName(request.getName());
            teacher.setEmployeeId(request.getEmployeeId());
            teacher.setDepartment(request.getDepartment());

            teacherRepository.save(teacher);
        }
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if (user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }

    public void generateResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = java.util.UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(java.time.LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String subject = "Password Reset Request - ERP System";
        String body = "Hello " + user.getName() + ",\n\n" +
                "You requested a password reset. Please use the token below:\n\n" +
                "Token: " + token + "\n\n" +
                "This token expires in 15 minutes.\n" +
                "If you did not request this, please ignore this email.";

        emailService.sendEmail(user.getEmail(), subject, body);
    }
}