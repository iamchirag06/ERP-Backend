package com.edu.erpbackend.model.academic;

import com.edu.erpbackend.model.users.Teacher;
import com.edu.erpbackend.model.operations.Branch;
import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "timetable_entries")
public class TimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Who and What?
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // When?
    @Enumerated(EnumType.STRING)
    private DayOfWeek day; // MONDAY, TUESDAY... (Java built-in or Custom Enum)

    private LocalTime startTime; // e.g., 10:00
    private LocalTime endTime;   // e.g., 11:00

    // Where and For Whom?
    private String roomNumber; // e.g., "LH-101" or "Computer Lab 2"

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    private Integer semester;
    private String section; // Optional: "A", "B"
}