package com.geovane.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class UrlStatsDto {
    private final Long id;
    @JsonProperty("url")
    private final String originalUrl;
    private final String shortCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    private final Instant createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    private final Instant updatedAt;
    private final long accessCount;

    public UrlStatsDto(Long id, String originalUrl, String shortCode, Instant createdAt, Instant updatedAt, long accessCount) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.accessCount = accessCount;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalUrl() {
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

    public long getAccessCount() {
        return accessCount;
    }
}
