package com.github.geovanegsfarias.controller;

import com.github.geovanegsfarias.dto.UrlPostRequest;
import com.github.geovanegsfarias.dto.UrlResponse;
import com.github.geovanegsfarias.dto.UrlStatsResponse;
import com.github.geovanegsfarias.exception.ErrorResponse;
import com.github.geovanegsfarias.mapper.UrlMapper;
import com.github.geovanegsfarias.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("v1")
@Slf4j
public class UrlController {
    private final UrlService service;
    private final UrlMapper mapper;

    @Autowired
    public UrlController(UrlService service, UrlMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Get URL by short code",
            description = "Returns details of a shortened URL.",
            tags = {"URL Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL details retrieved"),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    @GetMapping("/shorten/{shortCode}")
    public ResponseEntity<UrlResponse> getUrl(@PathVariable String shortCode) {
        log.debug("Received request to find URL with shortCode: {}", shortCode);

        var url = service.findByShortCodeOrThrowException(shortCode);

        UrlResponse urlResponse = mapper.toUrlResponse(url);

        return ResponseEntity.ok().body(urlResponse);
    }

    @Operation(
            summary = "Shorten a URL",
            description = "Creates a shortened URL and returns its details.",
            tags = {"URL Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "URL successfully created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> saveUrl(@Valid @RequestBody UrlPostRequest request) {
        log.debug("Received request to create shortened URL: {}", request);

        var urlToSave = mapper.toUrl(request);

        var savedUrl = service.save(urlToSave);

        var urlResponse = mapper.toUrlResponse(savedUrl);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/v1/{shortCode}")
                .buildAndExpand(savedUrl.getShortCode())
                .toUri();

        return ResponseEntity.created(location).body(urlResponse);
    }

    @Operation(
            summary = "Update URL",
            description = "Updates the original URL of an existing short code.",
            tags = {"URL Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "URL successfully updated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    @PutMapping("/shorten/{shortCode}")
    public ResponseEntity<Void> updateUrl(@PathVariable String shortCode, @Valid @RequestBody UrlPostRequest request) {
        log.debug("Received request to update URL with shortCode: {}", shortCode);

        var urlToUpdate = mapper.toUrl(request);

        urlToUpdate.setShortCode(shortCode);

        service.update(urlToUpdate);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete URL",
            description = "Deletes a shortened URL by its short code.",
            tags = {"URL Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "URL successfully deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    @DeleteMapping("/shorten/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        log.debug("Received request to delete URL with shortCode: {}", shortCode);

        service.delete(shortCode);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get URL stats",
            description = "Returns access statistics for a shortened URL.",
            tags = {"URL Statistics"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL statistics retrieved"),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    @GetMapping("/shorten/{shortCode}/stats")
    public ResponseEntity<UrlStatsResponse> urlStats(@PathVariable String shortCode) {
        log.debug("Received request to retrieve URL stats for shortCode: {}", shortCode);

        var url = service.findByShortCodeOrThrowException(shortCode);

        var urlStatsResponse = mapper.toUrlStatsResponse(url);

        return ResponseEntity.ok().body(urlStatsResponse);
    }

    @Operation(
            summary = "Redirect to URL",
            description = "Redirects to the URL and increments the access count.",
            tags = {"URL Redirection"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect to original URL"),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectTo(@PathVariable String shortCode) {
        log.debug("Received request to redirect using shortCode: {}", shortCode);

        var urlToRedirect = service.incrementAccessCountAndReturnUrl(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, urlToRedirect).build();
    }
}