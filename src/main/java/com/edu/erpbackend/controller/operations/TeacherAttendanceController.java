package com.edu.erpbackend.controller.operations;

import com.edu.erpbackend.dto.StudentLiteResponse;
import com.edu.erpbackend.dto.SubjectLiteResponse;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teacher/attendance")
@RequiredArgsConstructor
public class TeacherAttendanceController {

    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    // ✅ Teacher sees only their assigned subjects (small payload)
    @GetMapping("/subjects")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<SubjectLiteResponse>> getMySubjects() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Subject> subjects = subjectRepository.findByTeacherId(user.getId());

        List<SubjectLiteResponse> response = subjects.stream()
                .map(s -> SubjectLiteResponse.builder()
                        .subjectId(s.getId())
                        .name(s.getName())
                        .code(s.getCode())
                        .semester(s.getSemester())
                        .branchId(s.getBranch() != null ? s.getBranch().getId() : null)
                        .branchName(s.getBranch() != null ? s.getBranch().getName() : null)
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }

    // ✅ Teacher gets the roster for that subject (branch+semester derived server-side)
    @GetMapping("/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<StudentLiteResponse>> getStudentsForSubject(@RequestParam UUID subjectId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // ✅ Security: teacher can only access their subject roster
        if (subject.getTeacher() == null || subject.getTeacher().getId() == null
                || !subject.getTeacher().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: subject not assigned to you");
        }

        UUID branchId = subject.getBranch().getId();
        Integer semester = subject.getSemester();

        List<Student> students = studentRepository.findByBranchIdAndSemester(branchId, semester);

        List<StudentLiteResponse> response = students.stream()
                .map(st -> StudentLiteResponse.builder()
                        .studentId(st.getId())
                        .name(st.getName())
                        .rollNo(st.getRollNo())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }
}