package com.edu.erpbackend.service.academic;

import com.edu.erpbackend.dto.AssignmentRequest;
import com.edu.erpbackend.dto.GradeRequest;
import com.edu.erpbackend.dto.SubmissionRequest;
import com.edu.erpbackend.model.academic.Assignment;
import com.edu.erpbackend.model.academic.Subject;
import com.edu.erpbackend.model.academic.Submission;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.Teacher;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.operations.AssignmentRepository;
import com.edu.erpbackend.repository.operations.SubjectRepository;
import com.edu.erpbackend.repository.operations.SubmissionRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.TeacherRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.edu.erpbackend.dto.SubmissionResponse;

import java.util.Optional;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
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

    // 1. Create Assignment (✅ Fixed: Added 'String attachmentUrl' parameter)
    public Assignment createAssignment(String email, AssignmentRequest request, String attachmentUrl) {
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

        // ✅ Now this works because we passed it in the method arguments
        assignment.setAttachmentUrl(attachmentUrl);

        return assignmentRepository.save(assignment);
    }

    public void submitAssignment(String email, SubmissionRequest request, String fileUrl) {
        // 1. Fetch User & Student (Your existing logic)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        // 2. Check if this student already submitted (THE NEW LOGIC)
        Optional<Submission> existing = submissionRepository.findByAssignmentIdAndStudentId(
                assignment.getId(),
                student.getId()
        );

        Submission submission;

        if (existing.isPresent()) {
            // 🔄 CASE A: Update Existing Submission (Overwrite)
            submission = existing.get();

            // Optional: If they resubmit, you might want to reset the grade?
             submission.setGrade(null);
             submission.setTeacherFeedback(null);
        } else {
            // 🆕 CASE B: Create New Submission
            submission = new Submission();
            submission.setAssignment(assignment);
            submission.setStudent(student);
        }

        // 3. Common Updates (Apply to both New and Old)
        // If they sent a file, use it. If they sent a link, use that.
        submission.setSubmissionLink(fileUrl != null ? fileUrl : request.getSubmissionLink());

        // Update the timestamp to NOW
        submission.setSubmittedAt(LocalDateTime.now());

        // 4. Check for Late Submission (Your existing logic)
        if (assignment.getDeadline() != null && LocalDateTime.now().isAfter(assignment.getDeadline())) {
            submission.setLate(true);
        } else {
            submission.setLate(false);
        }

        // 5. Save
        submissionRepository.save(submission);
    }

    public Submission gradeSubmission(GradeRequest request) {
        Submission submission = submissionRepository.findById(request.getSubmissionId()).orElseThrow();
        submission.setGrade(request.getGrade());
        submission.setTeacherFeedback(request.getFeedback());
        return submissionRepository.save(submission);
    }

    public List<Assignment> getAssignmentsBySubject(UUID subjectId) {
        return assignmentRepository.findBySubjectId(subjectId);
    }

    public List<SubmissionResponse> getSubmissionsForAssignment(UUID assignmentId) {
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);

        return submissions.stream().map(sub -> new SubmissionResponse(
                sub.getId(),
                sub.getStudent().getName(), // ✅ Fix 1: Removed .getUser() (Student IS A User)
                sub.getStudent().getRollNo(),
                sub.getSubmissionLink(),
                sub.getGrade() != null ? sub.getGrade().toString() : "Not Graded",
                sub.getTeacherFeedback(),
                sub.isLate(),
                sub.getSubmittedAt()
        )).collect(Collectors.toList());
    }
}