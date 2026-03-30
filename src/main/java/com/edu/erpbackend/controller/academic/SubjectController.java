package com.edu.erpbackend.controller.academic;

import com.edu.erpbackend.model.academic.Subject;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.operations.SubjectRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    // ✅ Add this dependency (reused, no new files)
    private final StudentRepository studentRepository;

    // ✅ 1) Teacher fetches only their assigned subjects (LIGHT payload)
    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getMySubjects() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Subject> subjects = subjectRepository.findByTeacherId(user.getId());

        List<Map<String, Object>> payload = subjects.stream().map(s -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", s.getId());
            m.put("name", s.getName());
            m.put("code", s.getCode());
            m.put("semester", s.getSemester());

            if (s.getBranch() != null) {
                m.put("branchId", s.getBranch().getId());
                m.put("branchName", s.getBranch().getName());
                m.put("branchCode", s.getBranch().getCode());
            } else {
                m.put("branchId", null);
                m.put("branchName", null);
                m.put("branchCode", null);
            }

            return m;
        }).toList();

        return ResponseEntity.ok(payload);
    }

    // ✅ 2) General endpoint but LIGHT payload (avoid returning List<Subject>)
    @GetMapping("/{branchId}/{semester}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getSubjectsByBranchAndSemester(
            @PathVariable UUID branchId,
            @PathVariable Integer semester
    ) {
        List<Subject> subjects = subjectRepository.findByBranchIdAndSemester(branchId, semester);

        List<Map<String, Object>> payload = subjects.stream().map(s -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", s.getId());
            m.put("name", s.getName());
            m.put("code", s.getCode());
            m.put("semester", s.getSemester());

            if (s.getBranch() != null) {
                m.put("branchId", s.getBranch().getId());
                m.put("branchName", s.getBranch().getName());
                m.put("branchCode", s.getBranch().getCode());
            } else {
                m.put("branchId", null);
                m.put("branchName", null);
                m.put("branchCode", null);
            }

            return m;
        }).toList();

        return ResponseEntity.ok(payload);
    }

    // ✅ 3) Teacher gets roster for a subject they own (minimal payload)
    // Frontend calls: /api/subjects/{subjectId}/students
    @GetMapping("/{subjectId}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getMySubjectStudents(@PathVariable UUID subjectId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // ✅ Security: teacher can only fetch students for their own subject
        if (subject.getTeacher() == null || subject.getTeacher().getId() == null
                || !subject.getTeacher().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Unauthorized: subject not assigned to you");
        }

        UUID branchId = subject.getBranch().getId();
        Integer semester = subject.getSemester();

        List<Student> students = studentRepository.findByBranchIdAndSemester(branchId, semester);

        // ✅ minimal student payload for attendance marking
        List<Map<String, Object>> payload = students.stream().map(st -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", st.getId());
            m.put("name", st.getName());
            m.put("rollNo", st.getRollNo());
            return m;
        }).toList();

        return ResponseEntity.ok(payload);
    }
}