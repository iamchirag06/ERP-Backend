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
}