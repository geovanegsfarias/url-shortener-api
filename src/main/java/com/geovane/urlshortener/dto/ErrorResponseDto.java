package com.geovane.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "ErrorResponse", description = "DTO for API error details")
public record ErrorResponseDto(
        @Schema(description = "HTTP status code", example = "404")
        int statusCode,

        @Schema(description = "Timestamp of the error", example = "2026-02-21T19:40:00Z")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant timestamp,

        @Schema(description = "Error message", example = "URL not found")
        String message,

        @Schema(description = "Request path that caused the error", example = "uri=/shorten/aB34zQ")
        String description
)
{}