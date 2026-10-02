package com.karan.studysessiontracker.service;

import com.karan.studysessiontracker.entity.Subject;
import com.karan.studysessiontracker.entity.User;
import com.karan.studysessiontracker.repository.SubjectRepository;
import org.springframework.stereotype.Service;

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

    
}
