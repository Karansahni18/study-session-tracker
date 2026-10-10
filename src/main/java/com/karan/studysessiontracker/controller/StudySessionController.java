package com.karan.studysessiontracker.controller;

import com.karan.studysessiontracker.dto.request.StudySessionRequest;
import com.karan.studysessiontracker.dto.response.PagedResponse;
import com.karan.studysessiontracker.dto.response.StudySessionResponse;
import com.karan.studysessiontracker.entity.StudySession;
import com.karan.studysessiontracker.entity.Subject;
import com.karan.studysessiontracker.entity.User;
import com.karan.studysessiontracker.exception.ResourceNotFoundException;
import com.karan.studysessiontracker.repository.UserRepository;
import com.karan.studysessiontracker.service.StudySessionService;
import com.karan.studysessiontracker.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
public class StudySessionController {

    private final StudySessionService studySessionService;
    private final SubjectService subjectService;
    private final UserRepository userRepository;

    public StudySessionController(StudySessionService studySessionService,
                                  SubjectService subjectService,
                                  UserRepository userRepository) {
        this.studySessionService = studySessionService;
        this.subjectService = subjectService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<StudySessionResponse> createSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody StudySessionRequest request) {

        User user = getCurrentUser(userDetails);
        Subject subject = subjectService.getSubjectByIdForUser(request.getSubjectId(), user);

        StudySession session = new StudySession();
        session.setTopic(request.getTopic());
        session.setDurationMinutes(request.getDurationMinutes());
        session.setSessionDate(request.getSessionDate());
        session.setNotes(request.getNotes());
        session.setUser(user);
        session.setSubject(subject);

        StudySession saved = studySessionService.createSession(session);
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<StudySessionResponse>> getMySessions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User user = getCurrentUser(userDetails);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));

        Page<StudySession> sessions = studySessionService.getSessionsForUser(user, pageable);

        PagedResponse<StudySessionResponse> response = new PagedResponse<>(
                sessions.getContent().stream().map(this::toResponse).toList(),
                sessions.getNumber(),
                sessions.getSize(),
                sessions.getTotalElements(),
                sessions.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudySessionResponse> getSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User user = getCurrentUser(userDetails);
        StudySession session = studySessionService.getSessionByIdForUser(id, user);
        return ResponseEntity.ok(toResponse(session));
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private StudySessionResponse toResponse(StudySession s) {
        return new StudySessionResponse(
                s.getId(),
                s.getTopic(),
                s.getDurationMinutes(),
                s.getSessionDate(),
                s.getNotes(),
                s.getSubject().getName());
    }

}