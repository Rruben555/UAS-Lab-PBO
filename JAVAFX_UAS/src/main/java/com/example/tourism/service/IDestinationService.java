package com.example.tourism.service;

import com.example.tourism.model.dto.DestinationDTO;
import com.example.tourism.model.dto.ImageDTO;

import java.io.File;
import java.util.List;


public interface IDestinationService {

    List<DestinationDTO> getAllDestinations();


    DestinationDTO getDestinationById(Long id);


    List<DestinationDTO> getDestinationsByCategory(String category);


    List<DestinationDTO> searchDestinations(String keyword);


    DestinationDTO createDestination(DestinationDTO destination);


    DestinationDTO updateDestination(Long id, DestinationDTO destination);

    String uploadImage(Long destinationId, File file);

    List<ImageDTO> getImages(Long destinationId);

    boolean deleteImage(Long imageId);

    boolean deleteDestination(Long id);
}