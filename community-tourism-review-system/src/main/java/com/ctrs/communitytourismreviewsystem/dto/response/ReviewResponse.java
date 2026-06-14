package com.ctrs.communitytourismreviewsystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewResponse {

    private Long id;

    private Integer rating;

    private String comment;

    private String username;
}