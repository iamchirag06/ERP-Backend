package com.edu.erpbackend.repository;


import com.edu.erpbackend.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    // Get attendance for a specific student
    List<Attendance> findByStudentId(UUID studentId);
}