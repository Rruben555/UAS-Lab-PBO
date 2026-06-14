package com.ctrs.communitytourismreviewsystem.mapper;

import com.ctrs.communitytourismreviewsystem.dto.response.DestinationResponse;
import com.ctrs.communitytourismreviewsystem.entity.Destination;
import com.ctrs.communitytourismreviewsystem.entity.DestinationImage;

import java.util.List;

public class DestinationMapper {

    private DestinationMapper() {
    }

    public static DestinationResponse toResponse(
            Destination destination,
            Long totalReviews
    ) {

        List<String> images =
                destination.getImages() == null
                        ? List.of()
                        : destination.getImages()
                        .stream()
                        .map(DestinationImage::getImagePath)
                        .toList();

        return DestinationResponse.builder()
                .id(destination.getId())
                .name(destination.getName())
                .category(destination.getCategory())
                .location(destination.getLocation())
                .description(destination.getDescription())
                .averageRating(destination.getAverageRating())
                .totalReviews(totalReviews)
                .images(images)
                .build();
    }
}