package com.ctrs.communitytourismreviewsystem.service;

import com.ctrs.communitytourismreviewsystem.dto.request.DestinationRequest;
import com.ctrs.communitytourismreviewsystem.dto.response.DestinationResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DestinationService {

    DestinationResponse createDestination(
            DestinationRequest request
    );

    DestinationResponse updateDestination(
            Long id,
            DestinationRequest request
    );

    void deleteDestination(Long id);

    DestinationResponse getDestinationById(Long id);

    List<DestinationResponse> getAllDestinations();

    String uploadImage(
            Long destinationId,
            MultipartFile file
    );

    void deleteImage(Long imageId);
}