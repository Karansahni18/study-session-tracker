package com.karan.studysessiontracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter 
@Setter 
@NoArgsConstructor
public class StudySessionRequest {
    
    @NotBlank(message = "Topic is required")
    @Size(max = 100, message = "Topic must not exceed 100 characters")
    private String topic;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be a positive number")
    private Integer durationMinutes;

    @NotNull(message = "Session date is required")
    private LocalDate sessionDate;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;
}
