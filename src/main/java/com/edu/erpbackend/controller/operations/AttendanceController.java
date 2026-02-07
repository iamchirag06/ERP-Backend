package com.edu.erpbackend.controller.operations;

import com.edu.erpbackend.dto.AttendanceRequest;
import com.edu.erpbackend.model.operations.Attendance;
import com.edu.erpbackend.model.operations.AttendanceStatus; // ✅ Import Enum
import com.edu.erpbackend.repository.academic.AttendanceRepository;
import com.edu.erpbackend.service.operations.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceRepository attendanceRepository;

    @PostMapping("/mark")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> markAttendance(@RequestBody AttendanceRequest request) {
        try {
            attendanceService.markAttendance(request);
            return ResponseEntity.ok("Attendance marked successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<Attendance>> getAttendance(@PathVariable UUID studentId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(studentId));
    }

    // ✅ FIXED UPDATE METHOD
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> updateAttendance(@PathVariable UUID id, @RequestParam("status") boolean isPresent) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));

        // 🛠️ FIX: Convert Boolean to Enum
        if (isPresent) {
            attendance.setStatus(AttendanceStatus.PRESENT);
        } else {
            attendance.setStatus(AttendanceStatus.ABSENT);
        }

        attendanceRepository.save(attendance);
        return ResponseEntity.ok("Attendance corrected successfully");
    }
}