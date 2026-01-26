package com.edu.erpbackend.service;

import com.edu.erpbackend.dto.RegisterRequest;
import com.edu.erpbackend.model.*;
import com.edu.erpbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository; // To check if email exists
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final BranchRepository branchRepository;

    public void register(RegisterRequest request) {
        // 1. Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // 2. Handle Logic based on Role
        if (request.getRole() == Role.STUDENT) {
            Student student = new Student();
            // User Fields (Inherited)
            student.setEmail(request.getEmail());
            student.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            student.setRole(Role.STUDENT);
            student.setName(request.getName()); // ✅ Set Name

            // Student Specific Fields
            student.setRollNo(request.getRollNo());
            student.setSemester(request.getSemester());
            // Note: Branch needs to be set separately via API or logic if needed right away
            if (request.getBranchId() != null) {
                Branch branch = branchRepository.findById(request.getBranchId())
                        .orElseThrow(() -> new RuntimeException("Branch not found"));
                student.setBranch(branch);
            }
            studentRepository.save(student);

        } else if (request.getRole() == Role.TEACHER) {
            Teacher teacher = new Teacher();
            // User Fields (Inherited)
            teacher.setEmail(request.getEmail());
            teacher.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            teacher.setRole(Role.TEACHER);
            teacher.setName(request.getName()); // ✅ Set Name

            // Teacher Specific Fields
            teacher.setEmployeeId(request.getEmployeeId()); // ✅ Set Employee ID
            teacher.setDepartment(request.getDepartment()); // ✅ Set Department

            teacherRepository.save(teacher);
        }
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        // Check if token is expired
        if (user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        // Update password and clear token
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

        // 📧 Construct the email
        String subject = "Password Reset Request - ERP System";
        String body = "Hello " + user.getName() + ",\n\n" +
                "You requested a password reset. Please use the token below:\n\n" +
                "Token: " + token + "\n\n" +
                "This token expires in 15 minutes.\n" +
                "If you did not request this, please ignore this email.";

        // 🚀 Send it!
        emailService.sendEmail(user.getEmail(), subject, body);
    }
}