package com.karan.studysessiontracker.controller;

import com.karan.studysessiontracker.entity.Subject;
import com.karan.studysessiontracker.entity.User;
import com.karan.studysessiontracker.repository.UserRepository;
import com.karan.studysessiontracker.service.SubjectService;
import com.karan.studysessiontracker.dto.request.SubjectRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.karan.studysessiontracker.exception.ResourceNotFoundException;
import com.karan.studysessiontracker.dto.response.SubjectResponse;

import java.util.List;

@RestController 
@RequestMapping("/api/subjects")
public class SubjectController {
    
    private final SubjectService subjectService;
    private final UserRepository userRepository;

    public SubjectController(SubjectService subjectService, UserRepository userRepository) {

        this.subjectService = subjectService;
        this.userRepository = userRepository;

    }

    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody SubjectRequest request) {

        User user = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new ResourceNotFoundException("User not found")); 

        Subject subject = new Subject();
        subject.setName(request.getName());
        subject.setUser(user);

        Subject saved = subjectService.createSubject(subject);
        return ResponseEntity.ok(new SubjectResponse(saved.getId(), saved.getName()));
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getMySubjects(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<SubjectResponse> subjects = subjectService.getSubjectsForUser(user).stream()
                .map(s -> new SubjectResponse(s.getId(), s.getName()))
                .toList();
        return ResponseEntity.ok(subjects);
    }

}
