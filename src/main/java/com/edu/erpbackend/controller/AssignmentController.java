package com.edu.erpbackend.controller;

import com.edu.erpbackend.dto.AssignmentRequest;
import com.edu.erpbackend.dto.GradeRequest;
import com.edu.erpbackend.dto.SubmissionRequest;
import com.edu.erpbackend.dto.SubmissionResponse;
import com.edu.erpbackend.model.Assignment;
import com.edu.erpbackend.model.NotificationType;
import com.edu.erpbackend.model.Submission;
import com.edu.erpbackend.repository.SubmissionRepository;
import com.edu.erpbackend.service.AssignmentService;
import com.edu.erpbackend.service.FileService;
import com.edu.erpbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final NotificationService notificationService;
    private final SubmissionRepository submissionRepository;
    private final FileService fileService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // ---------------- TEACHER ENDPOINTS ----------------

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createAssignment(
//            @RequestPart("data") String dataJson,
            @RequestPart("data") AssignmentRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        // 1. Manually convert String to Object (Works with any Content-Type)
//        AssignmentRequest request = objectMapper.readValue(dataJson, AssignmentRequest.class);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Upload File
        String fileUrl = fileService.saveFile(file, "Assignments");

        // 3. Create Assignment
        Assignment saved = assignmentService.createAssignment(email, request, fileUrl);

        // 4. Send Notification
        notificationService.sendToGroup(
                saved.getSubject().getBranch(),
                saved.getSubject().getSemester(),
                "New Assignment: " + saved.getTitle(),
                "Due: " + saved.getDeadline(),
                NotificationType.ASSIGNMENT,
                saved.getId(),
                fileUrl
        );

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/grade")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> gradeSubmission(@RequestBody GradeRequest request) {
        Submission graded = assignmentService.gradeSubmission(request);

        notificationService.sendToUser(
                graded.getStudent(),
                "Grade Updated",
                "You received " + request.getGrade() + " for " + graded.getAssignment().getTitle(),
                NotificationType.GRADE_UPDATE,
                graded.getId()
        );

        return ResponseEntity.ok("Grade updated!");
    }

    @GetMapping("/{assignmentId}/submissions")
    // @PreAuthorize("hasRole('TEACHER')") // Optional: Restrict to teachers
    public ResponseEntity<List<SubmissionResponse>> viewSubmissions(@PathVariable UUID assignmentId) {
        return ResponseEntity.ok(assignmentService.getSubmissionsForAssignment(assignmentId));
    }

    // ---------------- STUDENT ENDPOINTS ----------------

    @PostMapping(value = "/submit", consumes = {"multipart/form-data"})
    public ResponseEntity<?> submitAssignment(
            @RequestPart("data") String dataJson, // Accept as String
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Parse JSON
        SubmissionRequest request = objectMapper.readValue(dataJson, SubmissionRequest.class);

        // 2. Upload File (if provided) to "submissions" folder
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            fileUrl = fileService.saveFile(file, "submissions");
        }

        // 3. Save Submission
        assignmentService.submitAssignment(email, request, fileUrl);

        return ResponseEntity.ok("Assignment submitted successfully!");
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Assignment>> getAssignments(@PathVariable UUID subjectId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsBySubject(subjectId));
    }

    // POST /api/assignments/grade/{submissionId}
    @PostMapping("/grade/{submissionId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> gradeSubmission(
            @PathVariable UUID submissionId,
            @RequestParam("grade") String grade, // e.g., "A", "85/100"
            @RequestParam(value = "feedback", required = false) String feedback
    ) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        try {
            submission.setGrade(Integer.parseInt(grade));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Grade must be a number");
        }
        submission.setTeacherFeedback(feedback);

        submissionRepository.save(submission);

        // Optional: Send notification to student ("Your assignment has been graded")
        return ResponseEntity.ok("Graded successfully");
    }
}