package com.edu.erpbackend.controller;

import com.edu.erpbackend.dto.AttendanceRequest;
import com.edu.erpbackend.model.Attendance;
import com.edu.erpbackend.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 👈 Import this
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 🔒 SECURE: Only Teachers (and Admin) can mark attendance
    @PostMapping("/mark")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')") // ✅ Restricts this specific action
    public ResponseEntity<?> markAttendance(@RequestBody AttendanceRequest request) {
        try {
            attendanceService.markAttendance(request);
            return ResponseEntity.ok("Attendance marked successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔓 OPEN: Students need to access this to see their history
    // We allow Students, Teachers, and Admins to view records
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<Attendance>> getAttendance(@PathVariable UUID studentId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(studentId));
    }
}