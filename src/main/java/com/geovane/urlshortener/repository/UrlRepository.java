package com.geovane.urlshortener.repository;

import com.geovane.urlshortener.model.Url;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends CrudRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);
    void deleteByShortCode(String shortCode);
    @Query(value = "SELECT nextval('urls_id_seq')", nativeQuery = true)
    Long getNextIdValue();
}