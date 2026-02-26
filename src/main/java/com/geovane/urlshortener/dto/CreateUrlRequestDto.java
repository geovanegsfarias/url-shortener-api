package com.geovane.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Schema(name = "UrlRequest", description = "DTO for creating or updating a shortened URL")
public record CreateUrlRequestDto (
        @Schema(description = "The URL to be shortened", example = "https://github.com/geovanegsfarias")
        @NotBlank(message = "URL must not be blank.")
        @URL(message = "URL is invalid.")
        String url
)
{}