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

    // Change return type to 'void' because Admin doesn't need the Student's token
    public void register(RegisterRequest request) {

        // 1. Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        User userToSave;

        // 2. Create the Specific Object
        if (request.getRole() == Role.STUDENT) {
            Student student = new Student();

            // --- Student Fields ---
            student.setRollNo(request.getRollNo());
            student.setBatch(request.getBatch());
            student.setSemester(request.getSemester());
            student.setBranch(branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found")));

            // Enhanced Fields
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

            // --- Teacher Fields ---
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

        // 3. Common Fields
        userToSave.setName(request.getName());
        userToSave.setEmail(request.getEmail());
        // Admin sets the password (e.g., "welcome123")
        userToSave.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userToSave.setRole(request.getRole());

        // 4. Save Only (No Token Generation)
        userRepository.save(userToSave);
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

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Generate Token
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15)); // 15 min expiry
        userRepository.save(user);

        // 2. Create the Link (Change this URL when you build your Frontend!)
        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        // 3. Create Professional HTML Body
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

        // 4. Send Email
        emailService.sendHtmlEmail(user.getEmail(), "Reset Your Password", htmlContent);
    }
}