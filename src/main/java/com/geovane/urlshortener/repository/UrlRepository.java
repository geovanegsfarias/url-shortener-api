package com.geovane.urlshortener.repository;

import com.geovane.urlshortener.model.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    Optional<UrlEntity> findUrlByShortCode(String shortCode);
    void deleteUrlByShortCode(String shortCode);
}