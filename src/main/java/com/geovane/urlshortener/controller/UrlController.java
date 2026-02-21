package com.geovane.urlshortener.controller;

import com.geovane.urlshortener.dto.UrlRequestDto;
import com.geovane.urlshortener.dto.UrlResponseDto;
import com.geovane.urlshortener.dto.UrlStatsDto;
import com.geovane.urlshortener.exception.ShortCodeNotFoundException;
import com.geovane.urlshortener.mapper.UrlMapper;
import com.geovane.urlshortener.model.Url;
import com.geovane.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
public class UrlController {
    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/shorten/{shortCode}")
    public ResponseEntity<UrlResponseDto> getUrl(@PathVariable String shortCode) {
        Url url = urlService.findUrlByShortCode(shortCode);
        UrlResponseDto urlDto = UrlMapper.toDto(url);
        return ResponseEntity.status(HttpStatus.OK).body(urlDto);
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponseDto> saveUrl(@Valid @RequestBody UrlRequestDto urlRequestDto) {
        Url url = urlService.saveUrl(UrlMapper.toEntity(urlRequestDto));
        UrlResponseDto urlDto = UrlMapper.toDto(url);
        return ResponseEntity.status(HttpStatus.CREATED).body(urlDto);
    }

    @PutMapping("/shorten/{shortCode}")
    public ResponseEntity<UrlResponseDto> updateUrl(@PathVariable String shortCode, @Valid @RequestBody UrlRequestDto urlRequestDto) {
        Url url = urlService.findUrlByShortCode(shortCode);
        Url urlFound = urlService.updateUrl(url, urlRequestDto);
        UrlResponseDto urlDto = UrlMapper.toDto(urlFound);
        return ResponseEntity.status(HttpStatus.OK).body(urlDto);
    }

    @DeleteMapping("/shorten/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlService.findUrlByShortCode(shortCode);
        urlService.deleteUrlByShortCode(shortCode);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/shorten/{shortCode}/stats")
    public ResponseEntity<UrlStatsDto> urlStats(@PathVariable String shortCode) {
        Url url = urlService.findUrlByShortCode(shortCode);
        UrlStatsDto urlDto = UrlMapper.toStatusDto(url);
        return ResponseEntity.status(HttpStatus.OK).body(urlDto);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        Url url = urlService.findUrlByShortCode(shortCode);
        urlService.incrementAccessCount(url);
        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, url.getOriginalUrl()).build();
    }
}