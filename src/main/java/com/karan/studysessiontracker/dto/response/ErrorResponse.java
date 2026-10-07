package com.karan.studysessiontracker.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class ErrorResponse {
    
    private int status;
    private String message;
    private long timestamp;
    
}
