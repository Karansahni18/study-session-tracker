package com.karan.studysessiontracker.service;

import com.karan.studysessiontracker.entity.StudySession;
import com.karan.studysessiontracker.entity.User;
import com.karan.studysessiontracker.repository.StudySessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.karan.studysessiontracker.exception.ResourceNotFoundException;
import com.karan.studysessiontracker.exception.UnauthorizedAccessException;
import com.karan.studysessiontracker.dto.request.StudySessionRequest;
import com.karan.studysessiontracker.entity.Subject;
import org.springframework.transaction.annotation.Transactional;

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
        return studySessionRepository.findByUserOrderBySessionDateDescIdDesc(user, pageable);
    }

    public StudySession getSessionByIdForUser(Long sessionId, User user) {

        StudySession session = studySessionRepository.findById(sessionId)
        .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if(!session.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You do not have permission to access this session");
        }

        return session;
    }

    @Transactional
    public StudySession updateSession(Long sessionId, User user, Subject subject, StudySessionRequest request) {
        StudySession session = getSessionByIdForUser(sessionId, user);

        session.setTopic(request.getTopic());
        session.setDurationMinutes(request.getDurationMinutes());
        session.setSessionDate(request.getSessionDate());
        session.setNotes(request.getNotes());
        session.setSubject(subject);

        return studySessionRepository.save(session);
    }

    @Transactional
    public void deleteSession(Long sessionId, User user) {
        StudySession session = getSessionByIdForUser(sessionId, user);
        studySessionRepository.delete(session);
    }
}
