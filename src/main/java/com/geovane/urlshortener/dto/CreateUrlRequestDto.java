package com.geovane.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record CreateUrlRequestDto (
        @NotBlank(message = "URL must not be blank.")
        @URL(message = "URL is invalid.")
        String url
)
{}