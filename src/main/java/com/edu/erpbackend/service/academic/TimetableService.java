package com.edu.erpbackend.service.academic;

import com.edu.erpbackend.dto.TimetableRequest;
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
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public void addEntry(TimetableRequest request) {
        TimetableEntry entry = new TimetableEntry();

        // Fetch Entities with clear error messages
        entry.setSubject(subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found")));
        entry.setTeacher(teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found")));
        entry.setBranch(branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found")));

        // Set simple fields
        entry.setSemester(request.getSemester());
        entry.setRoomNumber(request.getRoomNumber());
        entry.setStartTime(request.getStartTime());
        entry.setEndTime(request.getEndTime());

        // Convert String to Enum safely
        try {
            entry.setDay(DayOfWeek.valueOf(request.getDay().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Day. Use MONDAY, TUESDAY, etc.");
        }

        timetableRepository.save(entry);
    }

    public List<TimetableEntry> getMySchedule(String email) {
        // 1. Find User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Handle Student Logic
        if (user.getRole() == Role.STUDENT) {
            Student student = studentRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Student profile not found"));

            // ✅ FIX: Check if Branch is null before accessing .getId()
            if (student.getBranch() == null) {
                throw new RuntimeException("Error: You are not assigned to any Branch. Please contact Admin.");
            }

            return timetableRepository.findByBranchIdAndSemesterOrderByDayAscStartTimeAsc(
                    student.getBranch().getId(),
                    student.getSemester()
            );
        }

        // 3. Handle Teacher Logic
        else if (user.getRole() == Role.TEACHER) {
            return timetableRepository.findByTeacherIdOrderByDayAscStartTimeAsc(user.getId());
        }

        // 4. Handle Invalid Role
        throw new RuntimeException("Invalid Role for Timetable Access");
    }
}