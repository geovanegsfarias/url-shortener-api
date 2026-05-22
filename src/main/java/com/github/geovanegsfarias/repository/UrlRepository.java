package com.github.geovanegsfarias.repository;

import com.github.geovanegsfarias.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findUrlByShortCode(String shortCode);
}