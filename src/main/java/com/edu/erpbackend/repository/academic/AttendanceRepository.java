package com.edu.erpbackend.repository.academic;

import com.edu.erpbackend.model.operations.Attendance;
import com.edu.erpbackend.model.operations.AttendanceStatus; // ✅ Import Enum
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    // Existing
    List<Attendance> findByStudentId(UUID studentId);

    // ✅ Dashboard Stats: Total classes
    long countByStudentId(UUID studentId);

    // ✅ Dashboard Stats: Count where status is PRESENT
    // (Old method 'countByStudentIdAndIsPresentTrue' will fail because field 'present' doesn't exist)
    long countByStudentIdAndStatus(UUID studentId, AttendanceStatus status);
}