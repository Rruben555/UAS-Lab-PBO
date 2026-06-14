package com.example.tourism.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDTO<T> {
    private Boolean success;
    private String message;
    private T data;
}