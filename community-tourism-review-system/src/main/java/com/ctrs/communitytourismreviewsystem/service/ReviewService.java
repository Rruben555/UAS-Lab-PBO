package com.ctrs.communitytourismreviewsystem.service;

import com.ctrs.communitytourismreviewsystem.dto.request.ReviewRequest;
import com.ctrs.communitytourismreviewsystem.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createOrUpdateReview(
            Long destinationId,
            String username,
            ReviewRequest request
    );

    List<ReviewResponse> getReviewsByDestination(
            Long destinationId
    );

    void deleteReview(
            Long reviewId,
            String username
    );
}