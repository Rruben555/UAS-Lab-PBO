package com.ctrs.communitytourismreviewsystem.controller;

import com.ctrs.communitytourismreviewsystem.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String filename
    ) {

        Resource resource =
                fileStorageService.loadFile(filename);

        return ResponseEntity.ok()
                .body(resource);
    }
}