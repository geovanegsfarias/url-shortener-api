package com.geovane.urlshortener.service;

import com.geovane.urlshortener.dto.UrlRequestDto;
import com.geovane.urlshortener.model.Url;
import com.geovane.urlshortener.repository.UrlRepository;
import com.geovane.urlshortener.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public Url saveUrl(Url url) {
        Instant now = Instant.now();
        url.setCreatedAt(now);
        url.setUpdatedAt(now);
        Url savedUrl = urlRepository.save(url);
        savedUrl.setShortCode(Base62Encoder.encoder(savedUrl.getId()));
        return urlRepository.save(savedUrl);
    }

    public Url updateUrl(Url url, UrlRequestDto urlRequestDto) {
        url.setOriginalUrl(urlRequestDto.getOriginalUrl());
        url.setUpdatedAt(Instant.now());
        return urlRepository.save(url);
    }

    public Optional<Url> findUrlById(Long id) {
        return urlRepository.findById(id);
    }

    public Optional<Url> findUrlByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    @Transactional
    public void deleteUrlByShortCode(String shortCode) {
        urlRepository.findByShortCode(shortCode).ifPresent(urlRepository::delete);
    }

    public void incrementAccessCount(Url url) {
        url.setAccessCount(url.getAccessCount() + 1);
        urlRepository.save(url);
    }
}
