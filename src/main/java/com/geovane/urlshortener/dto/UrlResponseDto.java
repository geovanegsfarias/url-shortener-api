package com.geovane.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record UrlResponseDto(
        Long id,

        String url,

        String shortCode,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant createdAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant updatedAt
)
{}