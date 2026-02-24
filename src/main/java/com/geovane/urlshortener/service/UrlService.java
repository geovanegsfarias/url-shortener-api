package com.geovane.urlshortener.service;

import com.geovane.urlshortener.model.UrlEntity;
import com.geovane.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlService {
    private final UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlEntity saveUrl(UrlEntity url) {
        return null;
    }

    public UrlEntity updateUrl(UrlEntity url, UrlEntity urlUpdate) {
        return null;
    }

    public void deleteUrlByShortCode(String shortCode) {
        //
    }

    public UrlEntity findUrlByShortCode(String shortCode) {
        return null;
    }

    public void incrementAccessCount(UrlEntity url) {
        //
    }

}