package com.example.tourism.service.impl;

import com.example.tourism.model.dto.DestinationDTO;
import com.example.tourism.model.dto.ImageDTO;
import com.example.tourism.service.ApiClient;
import com.example.tourism.service.IDestinationService;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

/**
 * DestinationServiceImpl - Backend mengembalikan response LANGSUNG (tanpa ApiResponseDTO wrapper)
 * GET /destinations         -> List<DestinationResponse>
 * GET /destinations/{id}    -> DestinationResponse
 * POST /destinations        -> DestinationResponse  (ADMIN only)
 * PUT /destinations/{id}    -> DestinationResponse  (ADMIN only)
 * DELETE /destinations/{id} -> void                 (ADMIN only)
 */
@Slf4j
public class DestinationServiceImpl implements IDestinationService {

    @Override
    public List<DestinationDTO> getAllDestinations() {
        try {
            // Backend returns List<DestinationResponse> directly (no wrapper)
            Type responseType = new TypeToken<List<DestinationDTO>>() {}.getType();
            List<DestinationDTO> destinations = ApiClient.get("/destinations", responseType, true);

            if (destinations != null) {
                log.info("Berhasil memuat {} destinasi", destinations.size());
                return destinations;
            }

            log.warn("Response null dari /destinations");
            return List.of();
        } catch (Exception e) {
            log.error("Error fetching destinations", e);
            return List.of();
        }
    }

    @Override
    public DestinationDTO getDestinationById(Long id) {
        try {
            // Backend returns DestinationResponse directly (no wrapper)
            DestinationDTO destination = ApiClient.get("/destinations/" + id, DestinationDTO.class, true);

            if (destination != null) {
                return destination;
            }

            log.warn("Destinasi dengan ID {} tidak ditemukan", id);
            return null;
        } catch (Exception e) {
            log.error("Error fetching destination by id: {}", id, e);
            return null;
        }
    }

    @Override
    public List<DestinationDTO> getDestinationsByCategory(String category) {
        try {
            Type responseType = new TypeToken<List<DestinationDTO>>() {}.getType();
            List<DestinationDTO> destinations = ApiClient.get("/destinations/category/" + category, responseType, true);

            if (destinations != null) {
                return destinations;
            }

            log.warn("Tidak ada destinasi untuk kategori: {}", category);
            return List.of();
        } catch (Exception e) {
            log.error("Error fetching destinations by category: {}", category, e);
            return List.of();
        }
    }

    @Override
    public List<DestinationDTO> searchDestinations(String keyword) {
        try {
            Type responseType = new TypeToken<List<DestinationDTO>>() {}.getType();
            List<DestinationDTO> destinations = ApiClient.get(
                    "/destinations/search?keyword=" + keyword, responseType, true);

            if (destinations != null) {
                return destinations;
            }

            log.warn("Tidak ada hasil pencarian untuk: {}", keyword);
            return List.of();
        } catch (Exception e) {
            log.error("Error searching destinations: {}", keyword, e);
            return List.of();
        }
    }

    @Override
    public DestinationDTO createDestination(DestinationDTO destination) {
        try {
            // Admin only - returns DestinationResponse directly
            DestinationDTO created = ApiClient.post("/destinations", destination, DestinationDTO.class, true);

            if (created != null) {
                return created;
            }

            log.warn("Gagal membuat destinasi");
            return null;
        } catch (Exception e) {
            log.error("Error creating destination", e);
            return null;
        }
    }

    @Override
    public DestinationDTO updateDestination(Long id, DestinationDTO destination) {
        try {
            // Admin only - returns DestinationResponse directly
            DestinationDTO updated = ApiClient.put("/destinations/" + id, destination, DestinationDTO.class);

            if (updated != null) {
                return updated;
            }

            log.warn("Gagal mengupdate destinasi dengan ID {}", id);
            return null;
        } catch (Exception e) {
            log.error("Error updating destination", e);
            return null;
        }
    }

    @Override
    public boolean deleteDestination(Long id) {
        try {
            // Admin only - returns void, DELETE with 200/204/202 is success
            ApiClient.delete("/destinations/" + id, Void.class);
            log.info("Destinasi dengan ID {} berhasil dihapus", id);
            return true;
        } catch (Exception e) {
            log.error("Error deleting destination", e);
            return false;
        }
    }

    @Override
    public String uploadImage(Long destinationId, java.io.File file) {
        try {
            String result = ApiClient.uploadImage("/destinations/" + destinationId + "/images", file);
            return result;
        } catch (Exception e) {
            log.error("Error uploading image", e);
            return null;
        }
    }

    @Override
    public List<ImageDTO> getImages(Long destinationId) {
        try {
            Type responseType = new TypeToken<List<ImageDTO>>() {}.getType();
            List<ImageDTO> images = ApiClient.get("/destinations/" + destinationId + "/images", responseType, true);
            return images != null ? images : List.of();
        } catch (Exception e) {
            log.error("Error fetching images", e);
            return List.of();
        }
    }

    @Override
    public boolean deleteImage(Long imageId) {
        try {
            ApiClient.delete("/destinations/images/" + imageId, Void.class);
            return true;
        } catch (Exception e) {
            log.error("Error deleting image", e);
            return false;
        }
    }
}
