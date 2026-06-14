package com.ctrs.communitytourismreviewsystem.mapper;

import com.ctrs.communitytourismreviewsystem.dto.response.ReviewResponse;
import com.ctrs.communitytourismreviewsystem.entity.Review;

public class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewResponse toResponse(Review review) {

        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .username(review.getUser().getUsername())
                .build();
    }
}