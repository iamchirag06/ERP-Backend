package com.edu.erpbackend.service.academic;

import com.edu.erpbackend.model.academic.Doubt;
import com.edu.erpbackend.model.academic.DoubtStatus;
import com.edu.erpbackend.model.academic.Solution;
import com.edu.erpbackend.model.academic.Subject;
import com.edu.erpbackend.model.operations.NotificationType;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.operations.DoubtRepository;
import com.edu.erpbackend.repository.operations.SolutionRepository;
import com.edu.erpbackend.repository.operations.SubjectRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import com.edu.erpbackend.service.operations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoubtService {

    private final DoubtRepository doubtRepository;
    private final SolutionRepository solutionRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 1. Post a Doubt (Only Students)
    public void createDoubt(String email, UUID subjectId, String title, String description, Integer bounty) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Student student = studentRepository.findById(user.getId()).orElseThrow();
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();

        Doubt doubt = new Doubt();
        doubt.setAsker(student);
        doubt.setSubject(subject);
        doubt.setTitle(title);
        doubt.setDescription(description);
        doubt.setBountyPoints(bounty != null ? bounty : 0);
        doubt.setStatus(DoubtStatus.OPEN);

        doubtRepository.save(doubt);
    }

    // 2. Post a Solution (✅ Both Students & Teachers)
    public void addSolution(String email, UUID doubtId, String content) {
        User solver = userRepository.findByEmail(email).orElseThrow();
        Doubt doubt = doubtRepository.findById(doubtId).orElseThrow();

        Solution solution = new Solution();
        solution.setDoubt(doubt);
        solution.setSolver(solver); // ✅ Links to generic User (Teacher or Student)
        solution.setContent(content);

        solutionRepository.save(solution);

        // Notify the asker (only if they aren't answering their own question)
        if (!doubt.getAsker().getId().equals(solver.getId())) {
            notificationService.sendToUser(
                    doubt.getAsker(),
                    "New Reply on Your Doubt",
                    solver.getName() + " replied to your doubt: \"" + doubt.getTitle() + "\"",
                    NotificationType.DOUBT_REPLY,
                    doubt.getId()
            );
        }
    }

    // 3. Accept a Solution (✅ Secured)
    public void acceptSolution(String email, UUID solutionId) {
        User currentUser = userRepository.findByEmail(email).orElseThrow();
        Solution solution = solutionRepository.findById(solutionId).orElseThrow();
        Doubt doubt = solution.getDoubt();

        // ✅ SECURITY FIX: Only the asker or an admin can accept the solution
        if (!doubt.getAsker().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Unauthorized: Only the student who asked the doubt can accept the solution.");
        }

        solution.setAccepted(true);
        solutionRepository.save(solution);

        // Update Doubt Status
        doubt.setStatus(DoubtStatus.SOLVED);
        doubtRepository.save(doubt);

        // Notify solver
        if (!solution.getSolver().getId().equals(currentUser.getId())) {
            notificationService.sendToUser(
                    solution.getSolver(),
                    "Your Answer Was Accepted! 🎉",
                    "Your solution for \"" + doubt.getTitle() + "\" was marked as accepted.",
                    NotificationType.DOUBT_REPLY,
                    doubt.getId()
            );
        }
    }

    // 4. Get Data
    public List<Doubt> getDoubtsBySubject(UUID subjectId) {
        return doubtRepository.findBySubjectIdOrderByCreatedAtDesc(subjectId);
    }

    public List<Solution> getSolutionsForDoubt(UUID doubtId) {
        return solutionRepository.findByDoubtIdOrderByCreatedAtAsc(doubtId);
    }

    // ✅ NEW: Get all doubts by a specific student
    public List<Doubt> getDoubtsByStudent(UUID studentId) {
        return doubtRepository.findByAskerIdOrderByCreatedAtDesc(studentId);
    }

    // ✅ NEW: Get all doubts across all subjects
    public List<Doubt> getAllDoubts() {
        return doubtRepository.findAllByOrderByCreatedAtDesc();
    }

    // ✅ NEW: Get all doubts for student's subjects (branch and semester)
    public List<Doubt> getDoubtsForStudentSubjects(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Student student = studentRepository.findById(user.getId()).orElseThrow();

        if (student.getBranch() == null || student.getSemester() == null) {
            return List.of();
        }

        // Get all subjects for student's branch and semester
        List<Subject> subjects = subjectRepository.findByBranchIdAndSemester(
                student.getBranch().getId(),
                student.getSemester()
        );

        // Get doubts for all these subjects
        List<Doubt> doubts = new java.util.ArrayList<>();
        for (Subject subject : subjects) {
            doubts.addAll(doubtRepository.findBySubjectIdOrderByCreatedAtDesc(subject.getId()));
        }
        return doubts;
    }

    // ✅ HELPER: Format doubts to lightweight payload
    public List<Map<String, Object>> formatDoubtsResponse(List<Doubt> doubts, boolean includeDescription) {
        return doubts.stream().map(d -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", d.getId().toString());
            m.put("title", d.getTitle());
            
            if (includeDescription) {
                m.put("description", d.getDescription());
            }
            
            m.put("status", d.getStatus().name());
            m.put("bountyPoints", d.getBountyPoints());
            
            // Subject info
            if (d.getSubject() != null) {
                m.put("subjectId", d.getSubject().getId().toString());
                m.put("subjectName", d.getSubject().getName());
            }
            
            // Asker info
            if (d.getAsker() != null) {
                m.put("askerId", d.getAsker().getId().toString());
                m.put("askerName", d.getAsker().getName());
            }
            
            return m;
        }).toList();
    }

    // ✅ NEW: Get all solutions for a student's doubts
    public List<Map<String, Object>> getSolutionsForStudent(UUID studentId) {
        List<Doubt> studentDoubts = doubtRepository.findByAskerIdOrderByCreatedAtDesc(studentId);
        
        List<Map<String, Object>> allSolutions = new java.util.ArrayList<>();
        for (Doubt doubt : studentDoubts) {
            List<Solution> solutions = solutionRepository.findByDoubtIdOrderByCreatedAtAsc(doubt.getId());
            
            for (Solution solution : solutions) {
                Map<String, Object> m = new java.util.LinkedHashMap<>();
                m.put("solutionId", solution.getId().toString());
                m.put("doubtId", doubt.getId().toString());
                m.put("doubtTitle", doubt.getTitle());
                m.put("content", solution.getContent());
                m.put("isAccepted", solution.getAccepted() != null && solution.getAccepted());
                
                if (solution.getSolver() != null) {
                    m.put("solverId", solution.getSolver().getId().toString());
                    m.put("solverName", solution.getSolver().getName());
                }
                
                if (doubt.getSubject() != null) {
                    m.put("subjectId", doubt.getSubject().getId().toString());
                    m.put("subjectName", doubt.getSubject().getName());
                }
                
                allSolutions.add(m);
            }
        }
        
        return allSolutions;
    }

    // ✅ NEW: Get solutions for current logged-in student
    public List<Map<String, Object>> getMyDoubtsWithSolutions(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return getSolutionsForStudent(user.getId());
    }
}