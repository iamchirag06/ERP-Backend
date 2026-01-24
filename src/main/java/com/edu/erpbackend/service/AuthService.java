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

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest req) {
        // 1. Check if email exists
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // 2. Encrypt Password
        String encodedPass = passwordEncoder.encode(req.getPassword());

        // 3. Save based on Role
        if (req.getRole() == Role.STUDENT) {
            Student student = new Student();
            student.setEmail(req.getEmail());
            student.setPasswordHash(encodedPass);
            student.setRole(Role.STUDENT);
            student.setRollNo(req.getRollNo());
            student.setSemester(req.getSemester());

            // Link Branch if provided (Optional logic for now)
            // Branch branch = branchRepository.findByCode(req.getBranchCode()).orElse(null);
            // student.setBranch(branch);

            return studentRepository.save(student);

        } else if (req.getRole() == Role.TEACHER) {
            Teacher teacher = new Teacher();
            teacher.setEmail(req.getEmail());
            teacher.setPasswordHash(encodedPass);
            teacher.setRole(Role.TEACHER);
            teacher.setEmployeeId(req.getEmployeeId());

            return teacherRepository.save(teacher);
        } else {
            throw new RuntimeException("Invalid Role");
        }
    }
}