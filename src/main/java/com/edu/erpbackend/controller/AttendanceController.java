package com.edu.erpbackend.controller;

import com.edu.erpbackend.dto.AttendanceRequest;
import com.edu.erpbackend.model.Attendance;
import com.edu.erpbackend.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // Only Teachers should call this
    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestBody AttendanceRequest request) {
        try {
            attendanceService.markAttendance(request);
            return ResponseEntity.ok("Attendance marked successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Students call this to see their own history
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Attendance>> getAttendance(@PathVariable UUID studentId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(studentId));
    }
}