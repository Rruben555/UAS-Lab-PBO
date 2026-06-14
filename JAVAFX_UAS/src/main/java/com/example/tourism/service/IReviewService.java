package com.example.tourism.service;

import com.example.tourism.model.dto.ReviewDTO;
import com.example.tourism.model.dto.request.ReviewRequest;
import java.util.List;


public interface IReviewService {

    List<ReviewDTO> getAllReviews();


    ReviewDTO getReviewById(Long id);


    List<ReviewDTO> getReviewsByDestination(Long destinationId);


    ReviewDTO getUserReviewForDestination(Long destinationId);


    ReviewDTO createReview(ReviewRequest request, Long destinationId);


    ReviewDTO updateReview(Long id, ReviewRequest request);


    boolean deleteReview(Long id);
}