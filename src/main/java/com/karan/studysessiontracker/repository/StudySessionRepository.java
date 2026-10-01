package com.karan.studysessiontracker.repository;

import com.karan.studysessiontracker.entity.StudySession;
import com.karan.studysessiontracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    Page<StudySession> findByUserOrderBySessionDateDesc(User user, Pageable pageable);
    
}
