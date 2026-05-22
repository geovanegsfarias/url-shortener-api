package com.github.geovanegsfarias.service;

import com.github.geovanegsfarias.exception.ShortCodeNotFoundException;
import com.github.geovanegsfarias.model.UrlEntity;
import com.github.geovanegsfarias.repository.UrlRepository;
import com.github.geovanegsfarias.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional
    public UrlEntity saveUrl(UrlEntity url) {
        url = urlRepository.save(url);
        url.setShortCode(Base62Encoder.encoder(url.getId()));
        return url;
    }

    public UrlEntity updateUrl(UrlEntity url, UrlEntity urlUpdate) {
        url.setUrl(urlUpdate.getUrl());
        return urlRepository.save(url);
    }

    @Transactional
    public void deleteUrlByShortCode(String shortCode) {
        urlRepository.deleteUrlByShortCode(shortCode);
    }

    public UrlEntity findUrlByShortCode(String shortCode) {
        Optional<UrlEntity> url = urlRepository.findUrlByShortCode(shortCode);
        if (url.isPresent()) {
            return url.get();
        }
        throw new ShortCodeNotFoundException("URL not found.");
    }

    public void incrementAccessCount(UrlEntity url) {
        url.setAccessCount(url.getAccessCount() + 1);
        urlRepository.save(url);
    }

}