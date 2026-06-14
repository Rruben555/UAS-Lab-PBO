package com.ctrs.communitytourismreviewsystem.service.impl;

import com.ctrs.communitytourismreviewsystem.dto.request.ReviewRequest;
import com.ctrs.communitytourismreviewsystem.dto.response.ReviewResponse;
import com.ctrs.communitytourismreviewsystem.entity.Destination;
import com.ctrs.communitytourismreviewsystem.entity.Review;
import com.ctrs.communitytourismreviewsystem.entity.Role;
import com.ctrs.communitytourismreviewsystem.entity.User;
import com.ctrs.communitytourismreviewsystem.exception.ResourceNotFoundException;
import com.ctrs.communitytourismreviewsystem.mapper.ReviewMapper;
import com.ctrs.communitytourismreviewsystem.repository.DestinationRepository;
import com.ctrs.communitytourismreviewsystem.repository.ReviewRepository;
import com.ctrs.communitytourismreviewsystem.repository.UserRepository;
import com.ctrs.communitytourismreviewsystem.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl
        implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    @Override
    public ReviewResponse createOrUpdateReview(
            Long destinationId,
            String username,
            ReviewRequest request
    ) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User tidak ditemukan"
                        ));

        Destination destination = destinationRepository
                .findById(destinationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Destination tidak ditemukan"
                        ));

        Review review = reviewRepository
                .findByUserIdAndDestinationId(
                        user.getId(),
                        destinationId
                )
                .orElse(null);

        if (review == null) {

            review = Review.builder()
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .user(user)
                    .destination(destination)
                    .build();

        } else {

            review.setRating(request.getRating());
            review.setComment(request.getComment());
        }

        reviewRepository.save(review);

        updateAverageRating(destination);

        return ReviewMapper.toResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByDestination(
            Long destinationId
    ) {

        return reviewRepository
                .findByDestinationId(destinationId)
                .stream()
                .map(ReviewMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteReview(
            Long reviewId,
            String username
    ) {

        Review review = reviewRepository
                .findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review tidak ditemukan"
                        ));

        User currentUser = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User tidak ditemukan"
                        ));

        boolean isOwner =
                review.getUser()
                        .getId()
                        .equals(currentUser.getId());

        boolean isAdmin =
                currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {

            throw new RuntimeException(
                    "Anda tidak berhak menghapus review ini"
            );
        }

        Destination destination =
                review.getDestination();

        reviewRepository.delete(review);

        updateAverageRating(destination);
    }

    private void updateAverageRating(
            Destination destination
    ) {

        List<Review> reviews =
                reviewRepository.findByDestinationId(
                        destination.getId()
                );

        double average = reviews
                .stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        destination.setAverageRating(average);

        destinationRepository.save(destination);
    }
}