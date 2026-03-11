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
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/stats")
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final SubmissionRepository submissionRepository;
    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;
    private final AssignmentRepository assignmentRepository;

    @GetMapping
    public ResponseEntity<DashboardStats> getDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DashboardStats.DashboardStatsBuilder builder = DashboardStats.builder();

        // ✅ Common: Unread notification count as "active notices"
        long unread = notificationRepository.countByRecipientIdAndIsReadFalse(user.getId());
        builder.activeNotices((int) unread);

        if (user.getRole() == Role.STUDENT) {

            // ✅ Attendance Percentage
            long total = attendanceRepository.countByStudentId(user.getId());
            long present = attendanceRepository.countByStudentIdAndStatus(
                    user.getId(), AttendanceStatus.PRESENT);
            double pct = total > 0 ? Math.round((present * 100.0 / total) * 10.0) / 10.0 : 0.0;
            builder.attendancePercentage(pct);

            // ✅ Pending Assignments = total for class - already submitted
            Student student = studentRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            long totalAssignments = assignmentRepository
                    .countByBranchAndSemester(student.getBranch(), student.getSemester());
            long submitted = submissionRepository.countByStudentId(user.getId());
            builder.pendingAssignments((int) Math.max(0, totalAssignments - submitted));

        } else if (user.getRole() == Role.TEACHER) {

            // ✅ Total students in the system
            builder.totalStudents((int) studentRepository.count());

            // ✅ Ungraded submissions for THIS teacher's assignments
            long ungraded = submissionRepository
                    .countByAssignment_TeacherIdAndGradeIsNull(user.getId());
            builder.ungradedAssignments((int) ungraded);

        } else if (user.getRole() == Role.ADMIN) {

            // ✅ Admin sees total students
            builder.totalStudents((int) studentRepository.count());
        }

        return ResponseEntity.ok(builder.build());
    }
}