package com.ctrs.communitytourismreviewsystem.controller;

import com.ctrs.communitytourismreviewsystem.dto.request.DestinationRequest;
import com.ctrs.communitytourismreviewsystem.dto.response.DestinationResponse;
import com.ctrs.communitytourismreviewsystem.dto.response.ImageResponse;
import com.ctrs.communitytourismreviewsystem.service.DestinationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;

    @GetMapping
    public List<DestinationResponse> getAllDestinations() {
        return destinationService.getAllDestinations();
    }

    @GetMapping("/{id}")
    public DestinationResponse getDestinationById(
            @PathVariable Long id
    ) {
        return destinationService.getDestinationById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DestinationResponse createDestination(
            @Valid @RequestBody DestinationRequest request
    ) {
        return destinationService.createDestination(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DestinationResponse updateDestination(
            @PathVariable Long id,
            @Valid @RequestBody DestinationRequest request
    ) {
        return destinationService.updateDestination(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDestination(
            @PathVariable Long id
    ) {
        destinationService.deleteDestination(id);
    }

    @PostMapping(
            value = "/{id}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String uploadImage(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file
    ) {
        return destinationService.uploadImage(id, file);
    }

    @GetMapping("/{id}/images")
    public List<ImageResponse> getImages(@PathVariable Long id) {
        return destinationService.getImages(id);
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(@PathVariable Long imageId) {
        destinationService.deleteImage(imageId);
    }
}