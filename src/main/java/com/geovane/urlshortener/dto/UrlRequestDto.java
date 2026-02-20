package com.geovane.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class UrlRequestDto {
    @JsonProperty("url")
    @URL
    @NotBlank
    private String originalUrl;

    public String getOriginalUrl() {
        return originalUrl;
    }
}
