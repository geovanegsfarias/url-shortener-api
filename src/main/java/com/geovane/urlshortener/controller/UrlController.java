package com.geovane.urlshortener.controller;

import com.geovane.urlshortener.dto.CreateUrlRequestDto;
import com.geovane.urlshortener.dto.UrlResponseDto;
import com.geovane.urlshortener.dto.UrlStatsResponseDto;
import com.geovane.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {
    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping(value = "/shorten/{shortCode}")
    public ResponseEntity<UrlResponseDto> getUrl(@PathVariable String shortCode) {
        return null;
    }

    @PostMapping(value = "/shorten")
    public ResponseEntity<UrlResponseDto> saveUrl(@Valid @RequestBody CreateUrlRequestDto request) {
        return null;
    }

    @PutMapping(value = "/shorten/{shortCode}")
    public ResponseEntity<UrlResponseDto> updateUrl(@PathVariable String shortCode, @Valid @RequestBody CreateUrlRequestDto request) {
        return null;
    }

    @DeleteMapping("/shorten/{shortCode}")
    public ResponseEntity<Object> deleteUrl(@PathVariable String shortCode) {
        return null;
    }

    @GetMapping(value = "/shorten/{shortCode}/stats")
    public ResponseEntity<UrlStatsResponseDto> urlStats(@PathVariable String shortCode) {
        return null;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Object> redirectTo(@PathVariable String shortCode) {
        return null;
    }
}