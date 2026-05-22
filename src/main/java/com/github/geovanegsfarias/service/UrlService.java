package com.github.geovanegsfarias.service;

import com.github.geovanegsfarias.exception.UrlNotFoundException;
import com.github.geovanegsfarias.model.Url;
import com.github.geovanegsfarias.repository.UrlRepository;
import com.github.geovanegsfarias.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlService {
    private final UrlRepository repository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.repository = urlRepository;
    }

    public Url findByShortCodeOrThrowException(String shortCode) {
        return repository.findUrlByShortCode(shortCode).orElseThrow(() -> new UrlNotFoundException("URL not found"));
    }

    @Transactional
    public Url save(Url urlToSave) {
        var savedUrl = repository.save(urlToSave);
        savedUrl.setShortCode(Base62Encoder.encoder(savedUrl.getId()));
        return savedUrl;
    }

    public void update(Url urlToUpdate) {
        var savedUrl = findByShortCodeOrThrowException(urlToUpdate.getShortCode());
        savedUrl.setUrl(urlToUpdate.getUrl());
        repository.save(savedUrl);
    }

    @Transactional
    public void delete(String shortCode) {
        var urlToDelete = findByShortCodeOrThrowException(shortCode);
        repository.delete(urlToDelete);
    }

    public String incrementAccessCountAndReturnUrl(String shortCode) {
        var url = findByShortCodeOrThrowException(shortCode);
        url.setAccessCount(url.getAccessCount() + 1);
        repository.save(url);
        return url.getUrl();
    }

}