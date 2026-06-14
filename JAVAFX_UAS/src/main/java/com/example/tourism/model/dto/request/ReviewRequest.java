package com.example.tourism.model.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
    private Integer rating;
    private String comment;
    private Long destinationId;
}