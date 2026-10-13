package com.karan.studysessiontracker.service;

import com.karan.studysessiontracker.entity.StudySession;
import com.karan.studysessiontracker.entity.User;
import com.karan.studysessiontracker.exception.ResourceNotFoundException;
import com.karan.studysessiontracker.exception.UnauthorizedAccessException;
import com.karan.studysessiontracker.repository.StudySessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudySessionServiceTest {

    @Mock
    private StudySessionRepository studySessionRepository;

    @InjectMocks
    private StudySessionService studySessionService;

    private User owner;
    private User stranger;
    private StudySession session;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "karan", "hashed", "USER");
        stranger = new User(2L, "tester2", "hashed", "USER");

        session = new StudySession();
        session.setId(10L);
        session.setUser(owner);
    }

    @Test
    void getSessionByIdForUser_returnsSession_whenUserIsOwner() {
        when(studySessionRepository.findById(10L)).thenReturn(Optional.of(session));

        StudySession result = studySessionService.getSessionByIdForUser(10L, owner);

        assertSame(session, result);
    }

    @Test
    void getSessionByIdForUser_throwsNotFound_whenSessionMissing() {
        when(studySessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> studySessionService.getSessionByIdForUser(99L, owner));
    }

    @Test
    void getSessionByIdForUser_throwsUnauthorized_whenUserIsNotOwner() {
        when(studySessionRepository.findById(10L)).thenReturn(Optional.of(session));

        assertThrows(UnauthorizedAccessException.class,
                () -> studySessionService.getSessionByIdForUser(10L, stranger));
    }

    @Test
    void deleteSession_deletesSession_whenUserIsOwner() {
        when(studySessionRepository.findById(10L)).thenReturn(Optional.of(session));

        studySessionService.deleteSession(10L, owner);

        verify(studySessionRepository).delete(session);
    }

    @Test
    void deleteSession_neverDeletes_whenUserIsNotOwner() {
        when(studySessionRepository.findById(10L)).thenReturn(Optional.of(session));

        assertThrows(UnauthorizedAccessException.class,
                () -> studySessionService.deleteSession(10L, stranger));

        verify(studySessionRepository, never()).delete(any());
    }

}