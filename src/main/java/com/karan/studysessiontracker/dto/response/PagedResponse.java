package com.karan.studysessiontracker.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    
}
