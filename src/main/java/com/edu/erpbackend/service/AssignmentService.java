package com.edu.erpbackend.service;

import com.edu.erpbackend.dto.AssignmentRequest;
import com.edu.erpbackend.dto.GradeRequest;
import com.edu.erpbackend.dto.SubmissionRequest;
import com.edu.erpbackend.model.*;
import com.edu.erpbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    // 1. Create Assignment (Teacher Only)
    public void createAssignment(String email, AssignmentRequest request) {
        // Find the Teacher who is logged in
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Teacher teacher = teacherRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        Assignment assignment = new Assignment();
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDeadline(request.getDeadline());
        assignment.setSubject(subject);
        assignment.setTeacher(teacher);

        assignmentRepository.save(assignment);
    }

    // 2. Submit Assignment (Student Only)
    public void submitAssignment(String email, SubmissionRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        // Check if already submitted? (Optional logic)

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmissionLink(request.getSubmissionLink());

        submissionRepository.save(submission);
    }

    // 3. Grade Submission (Teacher Only)
    public void gradeSubmission(GradeRequest request) {
        Submission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setGrade(request.getGrade());
        submission.setTeacherFeedback(request.getFeedback());

        submissionRepository.save(submission);
    }

    // 4. Get Assignments for a Subject
    public List<Assignment> getAssignmentsBySubject(UUID subjectId) {
        return assignmentRepository.findBySubjectId(subjectId);
    }

    // 5. Get Submissions for an Assignment (For Teacher View)
    public List<Submission> getSubmissionsForAssignment(UUID assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }
}