package com.edu.erpbackend.controller.operations;

import com.edu.erpbackend.dto.DashboardStats;
import com.edu.erpbackend.model.operations.AttendanceStatus;
import com.edu.erpbackend.model.users.Role;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.Teacher;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.academic.AttendanceRepository;
import com.edu.erpbackend.repository.academic.NotificationRepository;
import com.edu.erpbackend.repository.operations.AssignmentRepository;
import com.edu.erpbackend.repository.operations.SubmissionRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.TeacherRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final NotificationRepository notificationRepository;
    private final TeacherRepository teacherRepository;
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        // 1. Common Stat: Unread Notices
        long unreadNotices = notificationRepository.countByRecipientIdAndIsReadFalse(user.getId());

        if (user.getRole() == Role.STUDENT) {
            Student student = studentRepository.findById(user.getId()).orElseThrow();

            // 2. Student Stat: Attendance %
            long totalClasses = attendanceRepository.countByStudentId(student.getId());
            long presentClasses = attendanceRepository.countByStudentIdAndStatus(student.getId(), AttendanceStatus.PRESENT);
            double percent = totalClasses == 0 ? 0.0 : ((double) presentClasses / totalClasses) * 100;

            // 3. Student Stat: Pending Assignments (Total Class Work - My Submissions)
            long totalAssignments = assignmentRepository.countByBranchAndSemester(student.getBranch(), student.getSemester());
            long mySubmissions = submissionRepository.countByStudentId(student.getId());

            // Logic: If there are 10 assignments and I did 8, then 2 are pending.
            // Math.max ensures we don't return negative numbers if data is messy.
            long pendingAssignments = Math.max(0, totalAssignments - mySubmissions);

            return ResponseEntity.ok(DashboardStats.builder()
                    .attendancePercentage(percent)
                    .activeNotices((int) unreadNotices)
                    .pendingAssignments((int) pendingAssignments) // ✅ Real Data
                    .build());

        } else if (user.getRole() == Role.TEACHER) {
            Teacher teacher = teacherRepository.findById(user.getId()).orElseThrow();

            // 4. Teacher Stat: Ungraded Submissions
            long ungraded = submissionRepository.countByAssignment_TeacherIdAndGradeIsNull(teacher.getId());

            return ResponseEntity.ok(DashboardStats.builder()
                    .totalStudents((int) studentRepository.count())
                    .activeNotices((int) unreadNotices)
                    .ungradedAssignments((int) ungraded)
                    .build());
        }

        return ResponseEntity.ok(DashboardStats.builder().build());
    }
}