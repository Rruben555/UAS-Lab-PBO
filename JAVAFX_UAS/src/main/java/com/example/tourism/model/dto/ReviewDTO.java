package com.example.tourism.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private String username;
    private Long destinationId;
    private Long createdAt;
    private Long updatedAt;
}