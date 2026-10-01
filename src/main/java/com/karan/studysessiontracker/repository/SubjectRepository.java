package com.karan.studysessiontracker.repository;

import com.karan.studysessiontracker.entity.Subject;
import com.karan.studysessiontracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    List<Subject> findByUser(User user);
}
