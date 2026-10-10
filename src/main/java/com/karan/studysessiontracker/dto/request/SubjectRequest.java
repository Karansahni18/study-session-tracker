package com.karan.studysessiontracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter 
@Setter 
@NoArgsConstructor
public class SubjectRequest {
    
    @NotBlank(message = "Subject name is required")
    @Size(max = 50, message = "Subject name must not exceed 50 characters")
    private String name;
}
