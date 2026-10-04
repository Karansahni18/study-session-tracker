package com.karan.studysessiontracker.dto.response;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class StudySessionResponse {
    
    private Long id;
    private String topic;
    private Integer durationMinutes;
    private LocalDate sessionDate;
    private String notes;
    private String subjectName;
    
}
