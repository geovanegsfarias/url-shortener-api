package com.geovane.urlshortener.controller;

import com.geovane.urlshortener.dto.CreateUrlRequestDto;
import com.geovane.urlshortener.dto.UrlResponseDto;
import com.geovane.urlshortener.dto.UrlStatsResponseDto;
import com.geovane.urlshortener.mapper.UrlMapper;
import com.geovane.urlshortener.model.UrlEntity;
import com.geovane.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
        UrlEntity url = urlService.findUrlByShortCode(shortCode);
        UrlResponseDto response = UrlMapper.mapUrlEntityToUrlResponseDto(url);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/shorten")
    public ResponseEntity<UrlResponseDto> saveUrl(@Valid @RequestBody CreateUrlRequestDto request) {
        UrlEntity url = urlService.saveUrl(UrlMapper.mapCreateUrlRequestDtoToUrlEntity(request));
        UrlResponseDto response = UrlMapper.mapUrlEntityToUrlResponseDto(url);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{shortCode}")
                .buildAndExpand(url.getShortCode())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping(value = "/shorten/{shortCode}")
    public ResponseEntity<UrlResponseDto> updateUrl(@PathVariable String shortCode, @Valid @RequestBody CreateUrlRequestDto request) {
        UrlEntity url = urlService.findUrlByShortCode(shortCode);
        UrlEntity urlData = UrlMapper.mapCreateUrlRequestDtoToUrlEntity(request);
        url = urlService.updateUrl(url, urlData);
        UrlResponseDto response = UrlMapper.mapUrlEntityToUrlResponseDto(url);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/shorten/{shortCode}")
    public ResponseEntity<Object> deleteUrl(@PathVariable String shortCode) {
        urlService.findUrlByShortCode(shortCode);
        urlService.deleteUrlByShortCode(shortCode);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/shorten/{shortCode}/stats")
    public ResponseEntity<UrlStatsResponseDto> urlStats(@PathVariable String shortCode) {
        UrlEntity url = urlService.findUrlByShortCode(shortCode);
        UrlStatsResponseDto response = UrlMapper.mapUrlEntityToUrlStatsResponseDto(url);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Object> redirectTo(@PathVariable String shortCode) {
        UrlEntity url = urlService.findUrlByShortCode(shortCode);
        urlService.incrementAccessCount(url);
        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, url.getUrl()).build();
    }
}