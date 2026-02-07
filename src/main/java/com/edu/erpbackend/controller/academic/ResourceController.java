package com.edu.erpbackend.controller.academic;

import com.edu.erpbackend.model.academic.StudyMaterial;
import com.edu.erpbackend.model.users.Teacher;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.operations.StudyMaterialRepository;
import com.edu.erpbackend.repository.operations.SubjectRepository;
import com.edu.erpbackend.repository.users.TeacherRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import com.edu.erpbackend.service.common.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final StudyMaterialRepository studyMaterialRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    // ==========================================
    // 1. UPLOAD NOTES (Teacher Only)
    // ==========================================
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> uploadMaterial(
            @RequestParam("title") String title,
            @RequestParam("subjectId") UUID subjectId,
            @RequestParam("unitTag") String unitTag, // e.g., "Unit 1"
            @RequestParam(value = "description", required = false) String description,
            @RequestPart("file") MultipartFile file
    ) throws IOException {

        // 1. Get Logged in Teacher
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Teacher teacher = teacherRepository.findById(user.getId()).orElseThrow();

        // 2. Upload File
        String fileUrl = fileService.saveFile(file, "StudyMaterials");

        // 3. Save Entry
        StudyMaterial material = new StudyMaterial();
        material.setTitle(title);
        material.setDescription(description);
        material.setUnitTag(unitTag);
        material.setFileUrl(fileUrl);
        material.setFileType(file.getContentType());
        material.setSubject(subjectRepository.findById(subjectId).orElseThrow());
        material.setUploadedBy(teacher);

        studyMaterialRepository.save(material);

        return ResponseEntity.ok("Material uploaded successfully!");
    }

    // ==========================================
    // 2. GET NOTES (Student & Teacher)
    // ==========================================
    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<StudyMaterial>> getNotes(@PathVariable UUID subjectId) {
        return ResponseEntity.ok(studyMaterialRepository.findBySubjectIdOrderByUploadedAtDesc(subjectId));
    }
}