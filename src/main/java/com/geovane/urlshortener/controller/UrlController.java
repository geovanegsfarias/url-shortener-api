package com.geovane.urlshortener.controller;

import com.geovane.urlshortener.dto.UrlRequestDto;
import com.geovane.urlshortener.dto.UrlResponseDto;
import com.geovane.urlshortener.dto.UrlStatsDto;
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
        Optional<Url> url = urlService.findUrlByShortCode(shortCode);
        if (url.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        UrlResponseDto urlDto = UrlMapper.toDto(url.get());
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
        Optional<Url> url = urlService.findUrlByShortCode(shortCode);
        if (url.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Url urlFound = urlService.updateUrl(url.get(), urlRequestDto);
        UrlResponseDto urlDto = UrlMapper.toDto(urlFound);
        return ResponseEntity.status(HttpStatus.OK).body(urlDto);
    }

    @DeleteMapping("/shorten/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        Optional<Url> url = urlService.findUrlByShortCode(shortCode);
        if (url.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        urlService.deleteUrlByShortCode(shortCode);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/shorten/{shortCode}/stats")
    public ResponseEntity<UrlStatsDto> urlStats(@PathVariable String shortCode) {
        Optional<Url> url = urlService.findUrlByShortCode(shortCode);
        if (url.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        UrlStatsDto urlDto = UrlMapper.toStatusDto(url.get());
        return ResponseEntity.status(HttpStatus.OK).body(urlDto);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        Optional<Url> url = urlService.findUrlByShortCode(shortCode);
        if (url.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        urlService.incrementAccessCount(url.get());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url.get().getOriginalUrl()));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}