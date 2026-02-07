package com.edu.erpbackend.service.academic;

import com.edu.erpbackend.model.academic.Doubt;
import com.edu.erpbackend.model.academic.DoubtStatus;
import com.edu.erpbackend.model.academic.StudyMaterial;
import com.edu.erpbackend.model.academic.Subject;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.operations.DoubtRepository;
import com.edu.erpbackend.repository.operations.SolutionRepository;
import com.edu.erpbackend.repository.operations.SubjectRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoubtService {

    private final DoubtRepository doubtRepository;
    private final SolutionRepository solutionRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    // 1. Post a Doubt
    public void createDoubt(String email, UUID subjectId, String title, String description, Integer bounty) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Student student = studentRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("Student not found"));
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new RuntimeException("Subject not found"));

        Doubt doubt = new Doubt();
        doubt.setAsker(student);
        doubt.setSubject(subject);
        doubt.setTitle(title);
        doubt.setDescription(description);
        doubt.setBountyPoints(bounty != null ? bounty : 0);
        doubt.setStatus(DoubtStatus.OPEN);

        doubtRepository.save(doubt);
    }

    // 2. Post a Solution (Answer)
    public void addSolution(String email, UUID doubtId, String content) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Student solver = studentRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("Student profile not found"));
        Doubt doubt = doubtRepository.findById(doubtId).orElseThrow(() -> new RuntimeException("Doubt not found"));

        StudyMaterial.Solution solution = new StudyMaterial.Solution();
        solution.setDoubt(doubt);
        solution.setSolver(solver);
        solution.setContent(content);

        solutionRepository.save(solution);
    }

    // 3. Accept a Solution (Mark as Correct)
    public void acceptSolution(UUID solutionId) {
        StudyMaterial.Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new RuntimeException("Solution not found"));

        solution.setIsAccepted(true);
        solutionRepository.save(solution);

        // Update Doubt Status
        Doubt doubt = solution.getDoubt();
        doubt.setStatus(DoubtStatus.SOLVED);
        doubtRepository.save(doubt);
    }

    // 4. Get Data
    public List<Doubt> getDoubtsBySubject(UUID subjectId) {
        return doubtRepository.findBySubjectIdOrderByCreatedAtDesc(subjectId);
    }

    public List<StudyMaterial.Solution> getSolutionsForDoubt(UUID doubtId) {
        return solutionRepository.findByDoubtIdOrderByCreatedAtAsc(doubtId);
    }
}