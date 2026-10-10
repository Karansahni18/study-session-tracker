package com.karan.studysessiontracker.service;

import com.karan.studysessiontracker.entity.Subject;
import com.karan.studysessiontracker.entity.User;
import com.karan.studysessiontracker.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import com.karan.studysessiontracker.exception.ResourceNotFoundException;
import com.karan.studysessiontracker.exception.UnauthorizedAccessException;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
    
    public List<Subject> getSubjectsForUser(User user) {
        return subjectRepository.findByUser(user);
    }

    public Subject getSubjectByIdForUser(Long subjectId, User user) {

        Subject subject = subjectRepository.findById(subjectId)
        .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        if(!subject.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You do not have permission to use this subject");
        }

        return subject;
    }
}
