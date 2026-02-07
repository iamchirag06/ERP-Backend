package com.edu.erpbackend.service.operations;

import com.edu.erpbackend.dto.AttendanceRequest;
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
        // 1. Find the subject to ensure it exists and to get its Branch
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // 2. Fetch ONLY the students who belong to this Branch and Semester
        // (This replaces the old studentRepository.findAll() line)
        List<Student> classStudents = studentRepository.findByBranchIdAndSemester(
                subject.getBranch().getId(),
                request.getSemester()
        );

        // Safety check: If no students found, maybe the request had the wrong semester?
        if (classStudents.isEmpty()) {
            throw new RuntimeException("No students found for this Branch and Semester");
        }

        // 3. Mark Attendance for each student
        for (Student student : classStudents) {
            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setSubject(subject);
            attendance.setDate(request.getDate());

            // If their ID is in the "Present List", mark PRESENT. Otherwise ABSENT.
            if (request.getPresentStudentIds().contains(student.getId())) {
                attendance.setStatus(AttendanceStatus.PRESENT);
            } else {
                attendance.setStatus(AttendanceStatus.ABSENT);
            }

            attendanceRepository.save(attendance);
        }
    }

    public List<Attendance> getStudentAttendance(UUID studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

}