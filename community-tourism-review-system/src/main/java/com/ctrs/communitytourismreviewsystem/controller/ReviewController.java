package com.ctrs.communitytourismreviewsystem.controller;

import com.ctrs.communitytourismreviewsystem.dto.request.ReviewRequest;
import com.ctrs.communitytourismreviewsystem.dto.response.ReviewResponse;
import com.ctrs.communitytourismreviewsystem.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/destination/{destinationId}")
    public ReviewResponse createReview(
            @PathVariable Long destinationId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication
    ) {

        return reviewService.createOrUpdateReview(
                destinationId,
                authentication.getName(),
                request
        );
    }

    @GetMapping("/destination/{destinationId}")
    public List<ReviewResponse> getReviews(
            @PathVariable Long destinationId
    ) {
        return reviewService.getReviewsByDestination(
                destinationId
        );
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {

        reviewService.deleteReview(
                reviewId,
                authentication.getName()
        );
    }
}