package com.ctrs.communitytourismreviewsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DestinationResponse {

    private Long id;

    private String name;

    private String category;

    private String location;

    private String description;

    private Double averageRating;

    private Long totalReviews;

    private List<String> images;
}