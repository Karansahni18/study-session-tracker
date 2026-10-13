package com.karan.studysessiontracker.service;

import com.karan.studysessiontracker.entity.Subject;
import com.karan.studysessiontracker.entity.User;
import com.karan.studysessiontracker.exception.ResourceNotFoundException;
import com.karan.studysessiontracker.exception.UnauthorizedAccessException;
import com.karan.studysessiontracker.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    private User owner;
    private User stranger;
    private Subject subject;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "karan", "hashed", "USER");
        stranger = new User(2L, "tester2", "hashed", "USER");

        subject = new Subject();
        subject.setId(5L);
        subject.setName("Java");
        subject.setUser(owner);
    }

    @Test
    void getSubjectByIdForUser_returnsSubject_whenUserIsOwner() {
        when(subjectRepository.findById(5L)).thenReturn(Optional.of(subject));

        Subject result = subjectService.getSubjectByIdForUser(5L, owner);

        assertSame(subject, result);
    }

    @Test
    void getSubjectByIdForUser_throwsNotFound_whenSubjectMissing() {
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> subjectService.getSubjectByIdForUser(99L, owner));
    }

    @Test
    void getSubjectByIdForUser_throwsUnauthorized_whenUserIsNotOwner() {
        when(subjectRepository.findById(5L)).thenReturn(Optional.of(subject));

        assertThrows(UnauthorizedAccessException.class,
                () -> subjectService.getSubjectByIdForUser(5L, stranger));
    }

    @Test
    void getSubjectsForUser_asksRepositoryForThatUsersSubjects() {
        when(subjectRepository.findByUser(owner)).thenReturn(List.of(subject));

        List<Subject> result = subjectService.getSubjectsForUser(owner);

        assertSame(subject, result.get(0));
        verify(subjectRepository).findByUser(owner);
    }

}