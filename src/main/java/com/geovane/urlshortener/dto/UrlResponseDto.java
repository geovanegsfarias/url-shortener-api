package com.geovane.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class UrlResponseDto {
    private final Long id;
    @JsonProperty("url")
    private final String originalUrl;
    private final String shortCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    private final Instant createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    private final Instant updatedAt;

    public UrlResponseDto(Long id, String url, String shortCode, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.originalUrl = url;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
