package com.example.tourism.model.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DestinationDTO {
    private Long id;
    private String name;
    private String category;
    private String location;
    private String description;
    private Double averageRating;
    private Integer totalReviews;
    private List<String> images;
    private Long createdAt;
    private Long updatedAt;
}