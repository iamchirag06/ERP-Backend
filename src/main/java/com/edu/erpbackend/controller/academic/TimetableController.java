package com.edu.erpbackend.controller.academic;

import com.edu.erpbackend.dto.TimetableRequest;
import com.edu.erpbackend.model.academic.TimetableEntry;
import com.edu.erpbackend.service.academic.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    // ==========================================
    // 1. ADD ENTRY (Clean & Type-Safe)
    // ==========================================
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addEntry(@RequestBody TimetableRequest request) {
        try {
            timetableService.addEntry(request);
            return ResponseEntity.ok("Timetable entry added successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================
    // 2. GET MY TIMETABLE
    // ==========================================
    @GetMapping("/my-schedule")
    public ResponseEntity<?> getMySchedule() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            List<TimetableEntry> schedule = timetableService.getMySchedule(email);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}