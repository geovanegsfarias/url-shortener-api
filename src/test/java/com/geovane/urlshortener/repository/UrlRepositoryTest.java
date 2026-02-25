package com.geovane.urlshortener.repository;

import com.geovane.urlshortener.model.UrlEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Sql({"/schema.sql", "/data.sql"})
class UrlRepositoryTest {

    @Autowired
    UrlRepository urlRepository;

    @Test
    @DisplayName("Should get URL successfully from DB")
    void findUrlByShortCodeCase1() {
        Optional<UrlEntity> url = urlRepository.findUrlByShortCode("1");
        assertThat(url).isPresent();
        assertThat(url.get().getShortCode()).isEqualTo("1");
    }

    @Test
    @DisplayName("Shouldn't get URL when URL not exists in DB")
    void findUrlByShortCodeCase2() {
        Optional<UrlEntity> url = urlRepository.findUrlByShortCode("3");
        assertThat(url).isEmpty();
    }

    @Test
    @DisplayName("Should delete URL successfully from DB")
    void deleteUrlByShortCodeCase1() {
        urlRepository.deleteUrlByShortCode("2");
        Optional<UrlEntity> result = urlRepository.findUrlByShortCode("2");
        assertThat(result).isEmpty();
    }
}