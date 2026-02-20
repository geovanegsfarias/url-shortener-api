package com.geovane.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class UrlRequestDto {
    @JsonProperty("url")
    @URL(message = "URL is invalid.")
    @NotBlank(message = "URL must not be blank.")
    private String originalUrl;

    public String getOriginalUrl() {
        return originalUrl;
    }
}
