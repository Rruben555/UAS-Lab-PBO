package com.ctrs.communitytourismreviewsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DestinationRequest {

    @NotBlank(message = "Nama destinasi wajib diisi")
    private String name;

    @NotBlank(message = "Kategori wajib diisi")
    private String category;

    @NotBlank(message = "Lokasi wajib diisi")
    private String location;

    @NotBlank(message = "Deskripsi wajib diisi")
    private String description;
}