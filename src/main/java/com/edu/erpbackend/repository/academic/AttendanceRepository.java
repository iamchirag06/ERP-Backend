package com.edu.erpbackend.repository.academic;

import com.edu.erpbackend.model.operations.Attendance;
import com.edu.erpbackend.model.operations.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    List<Attendance> findByStudentId(UUID studentId);

    long countByStudentId(UUID studentId);

    long countByStudentIdAndStatus(UUID studentId, AttendanceStatus status);

    // ✅ NEW: Duplicate attendance guard
    boolean existsBySubjectIdAndDate(UUID subjectId, LocalDate date);
}