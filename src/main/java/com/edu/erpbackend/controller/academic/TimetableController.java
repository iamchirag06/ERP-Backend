package com.edu.erpbackend.controller.academic;

import com.edu.erpbackend.model.academic.TimetableEntry;
import com.edu.erpbackend.model.users.Role;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.academic.BranchRepository;
import com.edu.erpbackend.repository.operations.SubjectRepository;
import com.edu.erpbackend.repository.operations.TimetableRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.TeacherRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableRepository timetableRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    // ==========================================
    // 1. ADD ENTRY (Admin Only)
    // ==========================================
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addEntry(@RequestBody Map<String, Object> body) {
        // Extract Data
        UUID subjectId = UUID.fromString((String) body.get("subjectId"));
        UUID teacherId = UUID.fromString((String) body.get("teacherId"));
        UUID branchId = UUID.fromString((String) body.get("branchId"));
        String roomNumber = (String) body.get("roomNumber");
        Integer semester = (Integer) body.get("semester");

        // Time & Day Parsing
        DayOfWeek day = DayOfWeek.valueOf(((String) body.get("day")).toUpperCase()); // e.g. "MONDAY"
        LocalTime startTime = LocalTime.parse((String) body.get("startTime")); // "10:00"
        LocalTime endTime = LocalTime.parse((String) body.get("endTime"));     // "11:00"

        TimetableEntry entry = new TimetableEntry();
        entry.setSubject(subjectRepository.findById(subjectId).orElseThrow());
        entry.setTeacher(teacherRepository.findById(teacherId).orElseThrow());
        entry.setBranch(branchRepository.findById(branchId).orElseThrow());
        entry.setSemester(semester);
        entry.setDay(day);
        entry.setStartTime(startTime);
        entry.setEndTime(endTime);
        entry.setRoomNumber(roomNumber);

        timetableRepository.save(entry);
        return ResponseEntity.ok("Timetable entry added");
    }

    // ==========================================
    // 2. GET MY TIMETABLE (Smart View)
    // ==========================================
    @GetMapping("/my-schedule")
    public ResponseEntity<List<TimetableEntry>> getMySchedule() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user.getRole() == Role.STUDENT) {
            // Logic: Find Student -> Get Branch/Sem -> Get Timetable
            Student student = studentRepository.findById(user.getId()).orElseThrow();
            return ResponseEntity.ok(
                    timetableRepository.findByBranchIdAndSemesterOrderByDayAscStartTimeAsc(
                            student.getBranch().getId(),
                            student.getSemester()
                    )
            );
        } else if (user.getRole() == Role.TEACHER) {
            // Logic: Find Teacher -> Get Timetable
            return ResponseEntity.ok(
                    timetableRepository.findByTeacherIdOrderByDayAscStartTimeAsc(user.getId())
            );
        }
        return ResponseEntity.badRequest().build();
    }
}