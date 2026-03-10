package com.edu.erpbackend.service.operations;

import com.edu.erpbackend.dto.AttendanceRequest;
import com.edu.erpbackend.dto.AttendanceSummaryResponse;
import com.edu.erpbackend.model.academic.Subject;
import com.edu.erpbackend.model.operations.Attendance;
import com.edu.erpbackend.model.operations.AttendanceStatus;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.repository.academic.AttendanceRepository;
import com.edu.erpbackend.repository.operations.SubjectRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void markAttendance(AttendanceRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // ✅ FIX: Guard against duplicate attendance for same date + subject
        boolean alreadyMarked = attendanceRepository
                .existsBySubjectIdAndDate(request.getSubjectId(), request.getDate());
        if (alreadyMarked) {
            throw new RuntimeException("Attendance already marked for this subject on " + request.getDate());
        }

        List<Student> classStudents = studentRepository.findByBranchIdAndSemester(
                subject.getBranch().getId(),
                request.getSemester()
        );

        if (classStudents.isEmpty()) {
            throw new RuntimeException("No students found for this Branch and Semester");
        }

        // ✅ FIX: Build list first, then saveAll() — 1 SQL query instead of N
        List<Attendance> attendanceList = new ArrayList<>();
        for (Student student : classStudents) {
            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setSubject(subject);
            attendance.setDate(request.getDate());

            if (request.getPresentStudentIds().contains(student.getId())) {
                attendance.setStatus(AttendanceStatus.PRESENT);
            } else {
                attendance.setStatus(AttendanceStatus.ABSENT);
            }
            attendanceList.add(attendance);
        }

        attendanceRepository.saveAll(attendanceList); // ✅ 1 batch insert
    }

    public List<Attendance> getStudentAttendance(UUID studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    // ✅ NEW: Attendance summary per subject with percentage
    public List<AttendanceSummaryResponse> getAttendanceSummary(UUID studentId) {
        List<Attendance> all = attendanceRepository.findByStudentId(studentId);

        // Group by subject
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
                    .percentage(Math.round(percentage * 10.0) / 10.0) // round to 1 decimal
                    .build());
        }
        return summary;
    }
}