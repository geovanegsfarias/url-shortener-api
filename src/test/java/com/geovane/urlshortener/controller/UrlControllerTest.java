package com.geovane.urlshortener.controller;

import com.geovane.urlshortener.exception.ShortCodeNotFoundException;
import com.geovane.urlshortener.model.UrlEntity;
import com.geovane.urlshortener.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Test
    @DisplayName("GET /shorten/1 returns 200 Ok and a valid JSON")
    void getUrlCase1() throws Exception {
        UrlEntity mockUrl = new UrlEntity(1L, "https://www.google.com", "1");
        when(urlService.findUrlByShortCode("1")).thenReturn(mockUrl);

        mockMvc.perform(get("/shorten/1"))
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.url").value("https://www.google.com"),
                        jsonPath("$.shortCode").value("1")
                );

        verify(urlService, times(1)).findUrlByShortCode("1");
    }

    @Test
    @DisplayName("GET /shorten/1 returns 404 Not Found and URL not found message error")
    void getUrlCase2() throws Exception {
        when(urlService.findUrlByShortCode("1")).thenThrow(new ShortCodeNotFoundException("URL not found"));

        mockMvc.perform(get("/shorten/1"))
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isNotFound(),
                        jsonPath("$.statusCode").value(404),
                        jsonPath("$.message").value("URL not found"),
                        jsonPath("$.description").value("uri=/shorten/1")
                );

        verify(urlService, times(1)).findUrlByShortCode("1");
    }

    @Test
    @DisplayName("POST /shorten returns 201 Created and a valid JSON")
    void saveUrlCase1() throws Exception {
    UrlEntity mockUrl = new UrlEntity(1L, "https://www.google.com", "1");
    when(urlService.saveUrl(any(UrlEntity.class))).thenReturn(mockUrl);

    mockMvc.perform(post("/shorten")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"url\":\"https://www.google.com\"}"))
            .andExpectAll(
                            content().contentType(MediaType.APPLICATION_JSON),
                            status().isCreated(),
                            header().string("Location", "http://localhost/1"),
                            jsonPath("$.id").value(1),
                            jsonPath("$.url").value("https://www.google.com"),
                            jsonPath("$.shortCode").value("1")
                    );

    verify(urlService, times(1)).saveUrl(any(UrlEntity.class));
    }

    @Test
    @DisplayName("POST /shorten returns 400 Bad Request and URL is invalid message error")
    void saveUrlCase2() throws Exception {
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"url\"}"))
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.message").value("URL is invalid."),
                        jsonPath("$.description").value("uri=/shorten")
                );

        verify(urlService, never()).saveUrl(any(UrlEntity.class));
    }

    @Test
    @DisplayName("PUT /shorten/1 returns 200 Ok and a valid JSON")
    void updateUrlCase1() throws Exception {
        UrlEntity mockUrl = new UrlEntity(1L, "https://www.google.com", "1");
        when(urlService.findUrlByShortCode("1")).thenReturn(mockUrl);
        when(urlService.updateUrl(eq(mockUrl), any(UrlEntity.class))).thenReturn(new UrlEntity(1L, "https://www.youtube.com", "1"));

        mockMvc.perform(put("/shorten/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"https://www.youtube.com\"}"))
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.url").value("https://www.youtube.com"),
                        jsonPath("$.shortCode").value("1")
                );

        verify(urlService, times(1)).findUrlByShortCode("1");
        verify(urlService, times(1)).updateUrl(eq(mockUrl), any(UrlEntity.class));
    }

    @Test
    @DisplayName("PUT /shorten/1 returns 404 Not Found and URL not found message error")
    void updateUrlCase2() throws Exception {
        when(urlService.findUrlByShortCode("1")).thenThrow(new ShortCodeNotFoundException("URL not found"));

        mockMvc.perform(put("/shorten/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"https://www.youtube.com\"}"))
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isNotFound(),
                        jsonPath("$.statusCode").value(404),
                        jsonPath("$.message").value("URL not found"),
                        jsonPath("$.description").value("uri=/shorten/1")
                );

        verify(urlService, times(1)).findUrlByShortCode("1");
    }

    @Test
    @DisplayName("PUT /shorten/1 returns 400 Bad Request and URL is invalid message error")
    void updateUrlCase3() throws Exception {
        mockMvc.perform(put("/shorten/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"url\"}"))
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.message").value("URL is invalid."),
                        jsonPath("$.description").value("uri=/shorten/1")
                );

        verify(urlService, never()).updateUrl(any(UrlEntity.class), any(UrlEntity.class));
    }

    @Test
    @DisplayName("DELETE /shorten/1 returns 204 No Content")
    void deleteUrlCase1() throws Exception {
        UrlEntity mockUrl = new UrlEntity(1L, "https://www.google.com", "1");
        when(urlService.findUrlByShortCode("1")).thenReturn(mockUrl);
        doNothing().when(urlService).deleteUrlByShortCode("1");

        mockMvc.perform(delete("/shorten/1"))
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @DisplayName("DELETE /shorten/1 returns 404 Not Found and URL not found message error")
    void deleteUrlCase2() throws Exception {
        when(urlService.findUrlByShortCode("1")).thenThrow(new ShortCodeNotFoundException("URL not found"));

        mockMvc.perform(delete("/shorten/1"))
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isNotFound(),
                        jsonPath("$.statusCode").value(404),
                        jsonPath("$.message").value("URL not found"),
                        jsonPath("$.description").value("uri=/shorten/1")
                );

        verify(urlService, times(1)).findUrlByShortCode("1");
    }

    @Test
    @DisplayName("GET /shorten/1/stats returns 200 Ok and a valid JSON")
    void urlStatsCase1() throws Exception {
        UrlEntity mockUrl = new UrlEntity(1L, "https://www.google.com", "1", 6);
        when(urlService.findUrlByShortCode("1")).thenReturn(mockUrl);

        mockMvc.perform(get("/shorten/1/stats"))
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.url").value("https://www.google.com"),
                        jsonPath("$.shortCode").value("1"),
                        jsonPath("$.accessCount").value(6)
                );

        verify(urlService, times(1)).findUrlByShortCode("1");
    }

    @Test
    @DisplayName("GET /shorten/1/stats returns 404 Not Found and URL not found message error")
    void getStatsCase2() throws Exception {
        when(urlService.findUrlByShortCode("1")).thenThrow(new ShortCodeNotFoundException("URL not found"));

        mockMvc.perform(get("/shorten/1/stats"))
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isNotFound(),
                        jsonPath("$.statusCode").value(404),
                        jsonPath("$.message").value("URL not found"),
                        jsonPath("$.description").value("uri=/shorten/1/stats")
                );

        verify(urlService, times(1)).findUrlByShortCode("1");
    }

    @Test
    @DisplayName("GET /1 returns 302 Found and redirects to location")
    void redirectToCase1() throws Exception {
        UrlEntity mockUrl = new UrlEntity(1L, "https://www.google.com", "1");
        when(urlService.findUrlByShortCode("1")).thenReturn(mockUrl);

        mockMvc.perform(get("/1"))
                .andExpectAll(
                        status().isFound(),
                        header().string("Location", "https://www.google.com")

                );

        verify(urlService, times(1)).findUrlByShortCode("1");
        verify(urlService, times(1)).incrementAccessCount(mockUrl);
    }

    @Test
    @DisplayName("GET /1 returns 404 Not Found and URL not found message error")
    void redirectToCase2() throws Exception {
        when(urlService.findUrlByShortCode("1")).thenThrow(new ShortCodeNotFoundException("URL not found"));

        mockMvc.perform(get("/1"))
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isNotFound(),
                        jsonPath("$.statusCode").value(404),
                        jsonPath("$.message").value("URL not found"),
                        jsonPath("$.description").value("uri=/1")
                );

        verify(urlService, times(1)).findUrlByShortCode("1");
    }
}