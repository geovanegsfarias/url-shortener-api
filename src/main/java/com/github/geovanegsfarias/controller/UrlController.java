package com.github.geovanegsfarias.controller;

import com.github.geovanegsfarias.dto.CreateUrlRequestDto;
import com.github.geovanegsfarias.dto.ErrorResponseDto;
import com.github.geovanegsfarias.dto.UrlResponseDto;
import com.github.geovanegsfarias.dto.UrlStatsResponseDto;
import com.github.geovanegsfarias.mapper.UrlMapper;
import com.github.geovanegsfarias.model.UrlEntity;
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
    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @Operation(
            summary = "Get shortened URL details",
            description = "Retrieves information about the shortened URL, including the original URL and metadata.",
            tags = {"URL Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL details retrieved"),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))}
    )
    @GetMapping("/shorten/{shortCode}")
    public ResponseEntity<UrlResponseDto> getUrl(@PathVariable String shortCode) {
        log.debug("Received request to find URL with shortCode: {}", shortCode);

        UrlEntity url = urlService.findUrlByShortCode(shortCode);

        UrlResponseDto response = UrlMapper.mapUrlEntityToUrlResponseDto(url);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Create a shortened URL",
            description = "Creates a new shortened URL and returns its details. The resource location is included in the response header.",
            tags = {"URL Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "URL successfully created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))}
    )
    @PostMapping("/shorten")
    public ResponseEntity<UrlResponseDto> saveUrl(@Valid @RequestBody CreateUrlRequestDto request) {
        log.debug("Received request to create shortened URL: {}", request);

        UrlEntity url = urlService.saveUrl(UrlMapper.mapCreateUrlRequestDtoToUrlEntity(request));

        UrlResponseDto response = UrlMapper.mapUrlEntityToUrlResponseDto(url);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{shortCode}")
                .buildAndExpand(url.getShortCode())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(
            summary = "Update a shortened URL",
            description = "Updates the original URL associated with the specified short code.",
            tags = {"URL Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL successfully updated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))}
    )
    @PutMapping("/shorten/{shortCode}")
    public ResponseEntity<UrlResponseDto> updateUrl(@PathVariable String shortCode, @Valid @RequestBody CreateUrlRequestDto request) {
        log.debug("Received request to update URL with shortCode: {}", shortCode);

        UrlEntity url = urlService.findUrlByShortCode(shortCode);

        UrlEntity urlData = UrlMapper.mapCreateUrlRequestDtoToUrlEntity(request);

        url = urlService.updateUrl(url, urlData);

        UrlResponseDto response = UrlMapper.mapUrlEntityToUrlResponseDto(url);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Delete a shortened URL",
            description = "Deletes the shortened URL identified by the specified short code.",
            tags = {"URL Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "URL successfully deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))}
    )
    @DeleteMapping("/shorten/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        log.debug("Received request to delete URL with shortCode: {}", shortCode);

        urlService.findUrlByShortCode(shortCode);

        urlService.deleteUrlByShortCode(shortCode);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Get URL statistics",
            description = "Retrieves access statistics for the shortened URL, including the total number of visits.",
            tags = {"URL Statistics"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL statistics retrieved"),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))}
    )
    @GetMapping("/shorten/{shortCode}/stats")
    public ResponseEntity<UrlStatsResponseDto> urlStats(@PathVariable String shortCode) {
        log.debug("Received request to retrieve URL stats for shortCode: {}", shortCode);

        UrlEntity url = urlService.findUrlByShortCode(shortCode);

        UrlStatsResponseDto response = UrlMapper.mapUrlEntityToUrlStatsResponseDto(url);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Redirect to the original URL",
            description = "Redirects the client to the original URL and increments the access count.",
            tags = {"URL Redirection"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect to original URL"),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))}
    )
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectTo(@PathVariable String shortCode) {
        log.debug("Received request to redirect using shortCode: {}", shortCode);

        UrlEntity url = urlService.findUrlByShortCode(shortCode);

        urlService.incrementAccessCount(url);

        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, url.getUrl()).build();
    }
}