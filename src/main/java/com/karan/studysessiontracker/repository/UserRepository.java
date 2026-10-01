package com.karan.studysessiontracker.repository;

import com.karan.studysessiontracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>  {
    
    
}
