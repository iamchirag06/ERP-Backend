package com.edu.erpbackend.service.operations;

import com.edu.erpbackend.dto.AttendanceRequest;
import com.edu.erpbackend.dto.AttendanceSummaryResponse;
import com.edu.erpbackend.model.academic.Subject;
import com.edu.erpbackend.model.operations.Attendance;
import com.edu.erpbackend.model.operations.AttendanceStatus;
import com.edu.erpbackend.model.users.Role;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.academic.AttendanceRepository;
import com.edu.erpbackend.repository.operations.SubjectRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Transactional
    public void markAttendance(AttendanceRequest request) {

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // SECURITY: only assigned teacher (or admin) can mark this subject
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (loggedInUser.getRole() == Role.TEACHER) {
            if (subject.getTeacher() == null || subject.getTeacher().getId() == null
                    || !subject.getTeacher().getId().equals(loggedInUser.getId())) {
                throw new RuntimeException("Unauthorized: You are not assigned to teach this subject");
            }
        }

        // Duplicate attendance guard: same subject + same date
        boolean alreadyMarked = attendanceRepository
                .existsBySubjectIdAndDate(request.getSubjectId(), request.getDate());
        if (alreadyMarked) {
            throw new RuntimeException("Attendance already marked for this subject on " + request.getDate());
        }

        // ✅ Derive class roster from subject (semester comes from subject, not request)
        UUID branchId = subject.getBranch().getId();
        Integer semester = subject.getSemester();

        List<Student> classStudents = studentRepository.findByBranchIdAndSemester(branchId, semester);

        if (classStudents.isEmpty()) {
            throw new RuntimeException("No students found for this Branch and Semester");
        }

        List<Attendance> attendanceList = new ArrayList<>();
        for (Student student : classStudents) {
            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setSubject(subject);
            attendance.setDate(request.getDate());

            if (request.getPresentStudentIds() != null
                    && request.getPresentStudentIds().contains(student.getId())) {
                attendance.setStatus(AttendanceStatus.PRESENT);
            } else {
                attendance.setStatus(AttendanceStatus.ABSENT);
            }
            attendanceList.add(attendance);
        }

        attendanceRepository.saveAll(attendanceList);
    }

    public List<Attendance> getStudentAttendance(UUID studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    public List<AttendanceSummaryResponse> getAttendanceSummary(UUID studentId) {
        List<Attendance> all = attendanceRepository.findByStudentId(studentId);

        java.util.Map<Subject, List<Attendance>> grouped = new java.util.LinkedHashMap<>();
        for (Attendance a : all) {
            grouped.computeIfAbsent(a.getSubject(), k -> new ArrayList<>()).add(a);
        }

        List<AttendanceSummaryResponse> summary = new ArrayList<>();
        for (java.util.Map.Entry<Subject, List<Attendance>> entry : grouped.entrySet()) {
            Subject subject = entry.getKey();
            List<Attendance> records = entry.getValue();
            long total = records.size();
            long present = records.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                    .count();
            double percentage = total > 0 ? (present * 100.0) / total : 0.0;

            summary.add(AttendanceSummaryResponse.builder()
                    .subjectName(subject.getName())
                    .subjectCode(subject.getCode())
                    .totalClasses((int) total)
                    .presentClasses((int) present)
                    .percentage(Math.round(percentage * 10.0) / 10.0)
                    .build());
        }
        return summary;
    }
}