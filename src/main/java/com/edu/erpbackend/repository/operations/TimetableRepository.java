package com.edu.erpbackend.repository.operations;

import com.edu.erpbackend.model.academic.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableEntry, UUID> {

    // 1. For Students: "Show me the schedule for CSE Sem 5"
    List<TimetableEntry> findByBranchIdAndSemesterOrderByDayAscStartTimeAsc(UUID branchId, Integer semester);

    // 2. For Teachers: "Show me where I have to teach"
    List<TimetableEntry> findByTeacherIdOrderByDayAscStartTimeAsc(UUID teacherId);
    List<TimetableEntry> findByBranchIdAndSemesterOrderByDayAscStartTimeAsc(
        UUID branchId, Integer semester
    );
}
