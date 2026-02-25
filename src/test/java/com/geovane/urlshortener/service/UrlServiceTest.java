package com.geovane.urlshortener.service;

import com.geovane.urlshortener.exception.ShortCodeNotFoundException;
import com.geovane.urlshortener.model.UrlEntity;
import com.geovane.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    @DisplayName("Should save URL successfully in DB")
    void saveUrlCase1() {
        UrlEntity url = new UrlEntity("https://www.google.com");
        UrlEntity savedUrl = new UrlEntity("https://www.google.com");
        savedUrl.setId(1L);

        when(urlRepository.save(any(UrlEntity.class))).thenReturn(savedUrl);

        UrlEntity result = urlService.saveUrl(url);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("1", result.getShortCode());
        verify(urlRepository, times(1)).save(any(UrlEntity.class));
    }

    @Test
    @DisplayName("Should update existing URL successfully")
    void updateUrlCase1() {
        UrlEntity existingUrl = new UrlEntity("https://www.google.com");
        UrlEntity urlUpdate = new UrlEntity("https://www.youtube.com");

        when(urlRepository.save(any(UrlEntity.class))).thenReturn(existingUrl);

        UrlEntity result = urlService.updateUrl(existingUrl, urlUpdate);

        assertEquals("https://www.youtube.com", result.getUrl());
        verify(urlRepository, times(1)).save(any(UrlEntity.class));
    }

    @Test
    @DisplayName("Should call repository to delete URL by shortCode")
    void deleteUrlByShortCodeCase1() {
        String shortCode = "5";
        urlService.deleteUrlByShortCode("5");

        verify(urlRepository, times(1)).deleteUrlByShortCode("5");
    }

    @Test
    @DisplayName("Should find URL by shortCode successfully")
    void findUrlByShortCodeCase1() {
        UrlEntity url = new UrlEntity("https://www.google.com");
        when(urlRepository.findUrlByShortCode(any(String.class))).thenReturn(Optional.of(url));

        UrlEntity result = urlService.findUrlByShortCode("1");

        assertEquals(url, result);
        verify(urlRepository, times(1)).findUrlByShortCode("1");
    }

    @Test
    @DisplayName("Should throw ShortCodeNotFoundException")
    void findUrlByShortCodeCase2() {
        when(urlRepository.findUrlByShortCode(any(String.class)))
                .thenReturn(Optional.empty());

        assertThrows(ShortCodeNotFoundException.class, () -> urlService.findUrlByShortCode("1"));
        verify(urlRepository, times(1)).findUrlByShortCode("1");
    }

    @Test
    @DisplayName("Should increment acess count and save")
    void incrementAccessCountCase1() {
        UrlEntity url = new UrlEntity("https://www.google.com");
        url.setAccessCount(2);

        urlService.incrementAccessCount(url);

        assertEquals(3, url.getAccessCount());
        verify(urlRepository, times(1)).save(url);
    }
}