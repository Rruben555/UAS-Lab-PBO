package com.ctrs.communitytourismreviewsystem.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequest {

    @Min(value = 1, message = "Rating minimal 1")
    @Max(value = 5, message = "Rating maksimal 5")
    private Integer rating;

    @NotBlank(message = "Komentar tidak boleh kosong")
    private String comment;
}