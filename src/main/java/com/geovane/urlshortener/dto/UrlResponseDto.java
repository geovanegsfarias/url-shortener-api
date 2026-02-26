package com.geovane.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "UrlResponse", description = "DTO for details of a shortened URL")
public record UrlResponseDto(
        @Schema(description = "The identifier of the URL", example = "50000")
        Long id,

        @Schema(description = "The URL to be shortened", example = "https://github.com/geovanegsfarias")
        String url,

        @Schema(description = "The short code generated for the URL", example = "aB5zW")
        String shortCode,

        @Schema(description = "Creation timestamp", example = "2026-02-21T19:40:00Z")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant createdAt,

        @Schema(description = "Last update timestamp", example = "2026-02-21T20:00:00Z")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant updatedAt
)
{}