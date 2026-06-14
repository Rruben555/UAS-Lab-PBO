package com.ctrs.communitytourismreviewsystem.service.impl;

import com.ctrs.communitytourismreviewsystem.dto.request.DestinationRequest;
import com.ctrs.communitytourismreviewsystem.dto.response.DestinationResponse;
import com.ctrs.communitytourismreviewsystem.entity.Destination;
import com.ctrs.communitytourismreviewsystem.entity.DestinationImage;
import com.ctrs.communitytourismreviewsystem.exception.ResourceNotFoundException;
import com.ctrs.communitytourismreviewsystem.mapper.DestinationMapper;
import com.ctrs.communitytourismreviewsystem.repository.DestinationImageRepository;
import com.ctrs.communitytourismreviewsystem.repository.DestinationRepository;
import com.ctrs.communitytourismreviewsystem.repository.ReviewRepository;
import com.ctrs.communitytourismreviewsystem.service.DestinationService;
import com.ctrs.communitytourismreviewsystem.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinationServiceImpl
        implements DestinationService {

    private final DestinationRepository destinationRepository;
    private final DestinationImageRepository imageRepository;
    private final ReviewRepository reviewRepository;
    private final FileStorageService fileStorageService;

    @Override
    public DestinationResponse createDestination(
            DestinationRequest request
    ) {

        Destination destination = Destination.builder()
                .name(request.getName())
                .category(request.getCategory())
                .location(request.getLocation())
                .description(request.getDescription())
                .averageRating(0.0)
                .build();

        destinationRepository.save(destination);

        return DestinationMapper.toResponse(
                destination,
                0L
        );
    }

    @Override
    public DestinationResponse updateDestination(
            Long id,
            DestinationRequest request
    ) {

        Destination destination = destinationRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Destination tidak ditemukan"
                        ));

        destination.setName(request.getName());
        destination.setCategory(request.getCategory());
        destination.setLocation(request.getLocation());
        destination.setDescription(request.getDescription());

        destinationRepository.save(destination);

        long totalReviews =
                reviewRepository.countByDestinationId(id);

        return DestinationMapper.toResponse(
                destination,
                totalReviews
        );
    }

    @Override
    public void deleteDestination(Long id) {

        Destination destination = destinationRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Destination tidak ditemukan"
                        ));

        destinationRepository.delete(destination);
    }

    @Override
    public DestinationResponse getDestinationById(
            Long id
    ) {

        Destination destination = destinationRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Destination tidak ditemukan"
                        ));

        long totalReviews =
                reviewRepository.countByDestinationId(id);

        return DestinationMapper.toResponse(
                destination,
                totalReviews
        );
    }

    @Override
    public List<DestinationResponse> getAllDestinations() {

        return destinationRepository
                .findAll()
                .stream()
                .map(destination -> {

                    long totalReviews =
                            reviewRepository.countByDestinationId(
                                    destination.getId()
                            );

                    return DestinationMapper.toResponse(
                            destination,
                            totalReviews
                    );

                })
                .toList();
    }

    @Override
    public String uploadImage(
            Long destinationId,
            MultipartFile file
    ) {

        Destination destination = destinationRepository
                .findById(destinationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Destination tidak ditemukan"
                        ));

        String fileName =
                fileStorageService.storeFile(file);

        DestinationImage image =
                DestinationImage.builder()
                        .imagePath("/files/" + fileName)
                        .destination(destination)
                        .build();

        imageRepository.save(image);

        return fileName;
    }

    @Override
    public void deleteImage(Long imageId) {

        DestinationImage image =
                imageRepository.findById(imageId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Gambar tidak ditemukan"
                                ));

        imageRepository.delete(image);
    }
}