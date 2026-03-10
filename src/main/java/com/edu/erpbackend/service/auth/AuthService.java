package com.edu.erpbackend.service.auth;

import com.edu.erpbackend.dto.LoginRequest;
import com.edu.erpbackend.dto.LoginResponse;
import com.edu.erpbackend.dto.RegisterRequest;
import com.edu.erpbackend.model.users.Role;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.Teacher;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.academic.BranchRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.TeacherRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import com.edu.erpbackend.service.common.EmailService;
import com.edu.erpbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final BranchRepository branchRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ✅ Injected from application.properties — no more hardcoded URL
    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // ✅ Resolve profileImageUrl from Student or Teacher subtype
        String profileImageUrl = null;
        if (user instanceof Student s) {
            profileImageUrl = s.getProfileImageUrl();
        } else if (user instanceof Teacher t) {
            profileImageUrl = t.getProfileImageUrl();
        }

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(profileImageUrl)
                .build();
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        User userToSave;

        if (request.getRole() == Role.STUDENT) {
            Student student = new Student();
            student.setRollNo(request.getRollNo());
            student.setBatch(request.getBatch());
            student.setSemester(request.getSemester());
            student.setBranch(branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found")));
            student.setPhoneNumber(request.getPhoneNumber());
            student.setAddress(request.getAddress());
            student.setDob(request.getDob());
            student.setGuardianName(request.getGuardianName());
            student.setGuardianPhone(request.getGuardianPhone());
            student.setCgpa(request.getCgpa());
            student.setActiveBacklogs(request.getActiveBacklogs());
            student.setLinkedinProfile(request.getLinkedinProfile());
            userToSave = student;

        } else if (request.getRole() == Role.TEACHER) {
            Teacher teacher = new Teacher();
            teacher.setBranch(branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found")));
            teacher.setPhoneNumber(request.getPhoneNumber());
            teacher.setQualification(request.getQualification());
            teacher.setCabinNumber(request.getCabinNumber());
            teacher.setJoiningDate(request.getJoiningDate());
            if (request.getDesignation() != null) {
                try {
                    teacher.setDesignation(Student.Designation.valueOf(request.getDesignation().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    teacher.setDesignation(Student.Designation.ASSISTANT_PROFESSOR);
                }
            }
            userToSave = teacher;

        } else {
            userToSave = new User();
        }

        userToSave.setName(request.getName());
        userToSave.setEmail(request.getEmail());
        userToSave.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userToSave.setRole(request.getRole());
        userRepository.save(userToSave);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // ✅ Uses property value instead of hardcoded localhost
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        String htmlContent = """
        <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
            <h2 style="color: #0056b3;">Password Reset Request</h2>
            <p>Hello %s,</p>
            <p>You requested to reset your password. Click the button below to proceed:</p>
            <a href="%s" style="background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 10px 0;">
                Reset Password
            </a>
            <p>Or copy this link: <br> <small>%s</small></p>
            <p><em>This link expires in 15 minutes.</em></p>
        </div>
        """.formatted(user.getName(), resetLink, resetLink);

        emailService.sendHtmlEmail(user.getEmail(), "Reset Your Password", htmlContent);
    }

    // ✅ NEW: Used by /api/auth/me endpoint
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}