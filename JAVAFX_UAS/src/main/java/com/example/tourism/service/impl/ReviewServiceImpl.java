package com.example.tourism.service.impl;

import com.google.gson.reflect.TypeToken;
import com.example.tourism.model.dto.ReviewDTO;
import com.example.tourism.model.dto.request.ReviewRequest;
import com.example.tourism.service.ApiClient;
import com.example.tourism.service.IReviewService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReviewServiceImpl implements IReviewService {

    @Override
    public List<ReviewDTO> getAllReviews() {
        try {
            Type responseType = new TypeToken<List<ReviewDTO>>(){}.getType();
            List<ReviewDTO> reviews = ApiClient.get("/reviews", responseType, true);
            return reviews != null ? reviews : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting all reviews", e);
            return new ArrayList<>();
        }
    }

    @Override
    public ReviewDTO getReviewById(Long id) {
        try {
            Type responseType = new TypeToken<ReviewDTO>(){}.getType();
            return ApiClient.get("/reviews/" + id, responseType, false);
        } catch (Exception e) {
            log.error("Error getting review by id: {}", id, e);
            return null;
        }
    }

    @Override
    public List<ReviewDTO> getReviewsByDestination(Long destinationId) {
        try {
            Type responseType = new TypeToken<List<ReviewDTO>>(){}.getType();
            List<ReviewDTO> reviews = ApiClient.get("/reviews/destination/" + destinationId, responseType, false);
            return reviews != null ? reviews : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting reviews for destination: {}", destinationId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public ReviewDTO getUserReviewForDestination(Long destinationId) {
        try {
            Type responseType = new TypeToken<ReviewDTO>(){}.getType();
            return ApiClient.get("/reviews/destination/" + destinationId + "/mine", responseType, true);
        } catch (Exception e) {
            log.error("Error getting user review for destination: {}", destinationId, e);
            return null;
        }
    }

    @Override
    public ReviewDTO createReview(ReviewRequest request, Long destinationId) {
        try {
            Type responseType = new TypeToken<ReviewDTO>(){}.getType();
            return ApiClient.post("/reviews/destination/" + destinationId, request, responseType, true);
        } catch (Exception e) {
            log.error("Error creating review for destination: {}", destinationId, e);
            return null;
        }
    }

    @Override
    public ReviewDTO updateReview(Long id, ReviewRequest request) {
        try {
            Type responseType = new TypeToken<ReviewDTO>(){}.getType();
            return ApiClient.put("/reviews/" + id, request, responseType);
        } catch (Exception e) {
            log.error("Error updating review: {}", id, e);
            return null;
        }
    }

    @Override
    public boolean deleteReview(Long id) {
        try {
            Type responseType = new TypeToken<Boolean>(){}.getType();
            Boolean result = ApiClient.delete("/reviews/" + id, responseType);
            return result != null && result;
        } catch (Exception e) {
            log.error("Error deleting review: {}", id, e);
            return false;
        }
    }
}