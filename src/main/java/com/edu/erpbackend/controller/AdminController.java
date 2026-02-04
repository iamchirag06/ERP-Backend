package com.edu.erpbackend.controller;

import com.edu.erpbackend.dto.BranchRequest;
import com.edu.erpbackend.dto.RegisterRequest;
import com.edu.erpbackend.model.Branch;
import com.edu.erpbackend.model.Student;
import com.edu.erpbackend.repository.BranchRepository;
import com.edu.erpbackend.model.Role;
import com.edu.erpbackend.repository.StudentRepository;
import com.edu.erpbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.edu.erpbackend.model.Subject;
import com.edu.erpbackend.repository.SubjectRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final BranchRepository branchRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    // 🔒 Admin Only: Create Teacher
    @PostMapping("/add-teacher")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addTeacher(@RequestBody RegisterRequest request) {
        request.setRole(Role.TEACHER);
        try {
            authService.register(request);
            return ResponseEntity.ok("Teacher added successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔒 Admin Only: Create Student (NEW)
    @PostMapping("/add-student")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addStudent(@RequestBody RegisterRequest request) {
        request.setRole(Role.STUDENT); // Force Role

        if (request.getBatch() == null || request.getBatch().isEmpty()) {
            return ResponseEntity.badRequest().body("Batch is required (e.g., '2023-2027')");
        }
        try {
            authService.register(request); // This now carries the batch info
            return ResponseEntity.ok("Student added successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/add-branch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addBranch(@RequestBody BranchRequest request) {
        // Simple check to prevent duplicates
        if (branchRepository.findByName(request.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Branch '" + request.getName() + "' already exists!");
        }

        Branch branch = new Branch();
        branch.setName(request.getName());
        branch.setCode(request.getCode());
        Branch savedBranch = branchRepository.save(branch);

        return ResponseEntity.ok(savedBranch); // Returns the saved branch with ID
    }

    // 2. View All Branches (So you can copy the UUIDs)
    @GetMapping("/branches")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Branch>> getAllBranches() {
        return ResponseEntity.ok(branchRepository.findAll());
    }
    // 3. Create a Subject (e.g., "Data Structures" for CSE Sem 3)
    @PostMapping("/add-subject/{branchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSubject(@PathVariable java.util.UUID branchId, @RequestBody Subject subject) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        subject.setBranch(branch);
        return ResponseEntity.ok(subjectRepository.save(subject));
    }

    // 4. View Subjects (Useful for checking what you created)
    @GetMapping("/subjects/{branchId}/{semester}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Subject>> getSubjects(@PathVariable java.util.UUID branchId, @PathVariable Integer semester) {
        return ResponseEntity.ok(subjectRepository.findByBranchIdAndSemester(branchId, semester));
    }

    @PostMapping("/promote-batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promoteBatch(@RequestBody Map<String, Object> body) {
        String batch = (String) body.get("batch");

        if (batch == null) return ResponseEntity.badRequest().body("Batch is required");

        List<Student> students = studentRepository.findByBatch(batch);

        if (students.isEmpty()) return ResponseEntity.badRequest().body("No students found in batch " + batch);

        // Logic: Increment Semester
        for (Student s : students) {
            s.setSemester(s.getSemester() + 1);
        }
        studentRepository.saveAll(students);

        return ResponseEntity.ok("Promoted " + students.size() + " students in batch " + batch);
    }

    // PUT /api/admin/student/{id}
    @PutMapping("/student/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStudent(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Update fields if provided in the JSON body
        if (updates.containsKey("name")) student.setName((String) updates.get("name"));
        // Note: Changing email might require checking for duplicates in a real app
        if (updates.containsKey("email")) student.setEmail((String) updates.get("email"));
        if (updates.containsKey("rollNo")) student.setRollNo((String) updates.get("rollNo"));
        if (updates.containsKey("semester")) student.setSemester((Integer) updates.get("semester"));
        if (updates.containsKey("batch")) student.setBatch((String) updates.get("batch"));

        studentRepository.save(student);
        return ResponseEntity.ok("Student details updated successfully");
    }
}