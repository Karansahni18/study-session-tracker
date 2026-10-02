package com.karan.studysessiontracker.service;

import com.karan.studysessiontracker.entity.StudySession;
import com.karan.studysessiontracker.entity.User;
import com.karan.studysessiontracker.repository.StudySessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StudySessionService {

    private final StudySessionRepository studySessionRepository;

    public StudySessionService(StudySessionRepository studySessionRepository) {
        this.studySessionRepository = studySessionRepository;
    }

    public StudySession createSession(StudySession session) {
        return studySessionRepository.save(session);
    }

    public Page<StudySession> getSessionsForUser(User user, Pageable pageable) {
        return studySessionRepository.findByUserOrderBySessionDateDesc(user, pageable);
    }

    public StudySession getSessionByIdForUser(Long sessionId, User user) {

        StudySession session = studySessionRepository.findById(sessionId)
        .orElseThrow(() -> new RuntimeException("Session not found"));

        if(!session.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to access this session");
        }

        return session;
    }
}
