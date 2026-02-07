package com.edu.erpbackend.controller.users;

import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.Teacher;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.academic.BranchRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.TeacherRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not Authenticated");
        }
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);

        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<Student>> getStudents(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String batch
    ) {
        if (batch != null && branchId != null) {
            return ResponseEntity.ok(studentRepository.findByBatchAndBranchId(batch, branchId));
        } else if (batch != null) {
            return ResponseEntity.ok(studentRepository.findByBatch(batch));
        } else if (branchId != null && semester != null) {
            return ResponseEntity.ok(studentRepository.findByBranchIdAndSemester(branchId, semester));
        } else {
            return ResponseEntity.ok(studentRepository.findAll());
        }
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<Teacher>> getTeachers(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) Integer semester
    ) {
        if (branchId != null && semester != null) {
            return ResponseEntity.ok(teacherRepository.findByBranchAndSemester(branchId, semester));
        } else if (branchId != null) {
            // Updated logic to find teachers by Branch Code (Department)
            return branchRepository.findById(branchId)
                    .map(branch -> teacherRepository.findByDepartment(branch.getCode()))
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.ok(teacherRepository.findAll());
        }
    }
}