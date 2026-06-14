package com.ctrs.communitytourismreviewsystem.service.impl;

import com.ctrs.communitytourismreviewsystem.config.FileStorageConfig;
import com.ctrs.communitytourismreviewsystem.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl
        implements FileStorageService {

    private final FileStorageConfig fileStorageConfig;

    @Override
    public String storeFile(MultipartFile file) {

        try {

            Path uploadPath =
                    Paths.get(fileStorageConfig.getUploadDir());

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();

            Files.copy(
                    file.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Gagal upload file"
            );
        }
    }

    @Override
    public Resource loadFile(String filename) {

        try {

            Path filePath =
                    Paths.get(fileStorageConfig.getUploadDir())
                            .resolve(filename);

            Resource resource =
                    new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            }

            throw new RuntimeException(
                    "File tidak ditemukan"
            );

        } catch (MalformedURLException e) {

            throw new RuntimeException(
                    "File tidak ditemukan"
            );
        }
    }
}