package com.github.geovanegsfarias.service;

import com.github.geovanegsfarias.commons.UrlUtils;
import com.github.geovanegsfarias.exception.UrlNotFoundException;
import com.github.geovanegsfarias.repository.UrlRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UrlServiceTest {
    @Mock
    private UrlRepository repository;
    @InjectMocks
    private UrlService service;
    private final UrlUtils utils = new UrlUtils();

    @Test
    @DisplayName("findByShortCode returns a Url with given shortCode")
    @Order(1)
    void findByShortCode_ReturnsUrl_WhenSuccessful() {
        var expectedUrl = utils.newSavedUrl();
        
        BDDMockito.when(repository.findUrlByShortCode(expectedUrl.getShortCode())).thenReturn(Optional.of(expectedUrl));

        var returnedUrl = service.findByShortCodeOrThrowException(expectedUrl.getShortCode());

        Assertions.assertThat(returnedUrl).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("findByShortCode throws UrlNotFoundException when url is not found")
    @Order(2)
    void findByShortCode_ThrowsUrlNotFoundException_WhenUrlIsNotFound() {
        var expectedUrl = utils.newSavedUrl();

        BDDMockito.when(repository.findUrlByShortCode(expectedUrl.getShortCode())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findByShortCodeOrThrowException(expectedUrl.getShortCode()))
                .withMessage("URL not found")
                .isInstanceOf(UrlNotFoundException.class);
    }

    @Test
    @DisplayName("save creates a url")
    @Order(3)
    void save_CreatesUrl_WhenSuccessful() {
        var urlToSave = utils.newUrlToSave();
        var expectedSavedUrl = utils.newSavedUrl();
        
        BDDMockito.when(repository.save(urlToSave)).thenReturn(expectedSavedUrl);

        var savedUrl = service.save(urlToSave);

        Assertions.assertThat(savedUrl).isEqualTo(expectedSavedUrl).hasNoNullFieldsOrProperties();
        BDDMockito.verify(repository).save(urlToSave);
    }

    @Test
    @DisplayName("update updates a url")
    @Order(4)
    void update_UpdatesUrl_WhenSuccessful() {
        var urlToUpdate = utils.newSavedUrl();

        BDDMockito.when(repository.findUrlByShortCode(urlToUpdate.getShortCode())).thenReturn(Optional.of(urlToUpdate));
        BDDMockito.when(repository.save(urlToUpdate)).thenReturn(urlToUpdate);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(urlToUpdate));
        BDDMockito.verify(repository).save(urlToUpdate);
    }

    @Test
    @DisplayName("update throws UrlNotFoundException when url is not found")
    @Order(5)
    void update_throwsUrlNotFoundException_WhenUrlIsNotFound() {
        var urlToUpdate = utils.newSavedUrl();

        BDDMockito.when(repository.findUrlByShortCode(urlToUpdate.getShortCode())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(urlToUpdate))
                .withMessage("URL not found")
                .isInstanceOf(UrlNotFoundException.class);
    }

    @Test
    @DisplayName("delete removes a url")
    @Order(6)
    void delete_RemovesUrl_WhenSuccessful() {
        var urlToDelete = utils.newSavedUrl();

        BDDMockito.when(repository.findUrlByShortCode(urlToDelete.getShortCode())).thenReturn(Optional.of(urlToDelete));
        BDDMockito.doNothing().when(repository).delete(urlToDelete);

        Assertions.assertThatNoException().isThrownBy(() -> service.delete(urlToDelete.getShortCode()));
    }

    @Test
    @DisplayName("delete throws UrlNotFoundException when url is not found")
    @Order(7)
    void delete_throwsUrlNotFoundException_WhenUrlIsNotFound() {
        var urlToDelete = utils.newSavedUrl();

        BDDMockito.when(repository.findUrlByShortCode(urlToDelete.getShortCode())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(urlToDelete.getShortCode()))
                .withMessage("URL not found")
                .isInstanceOf(UrlNotFoundException.class);
    }

    @Test
    @DisplayName("incrementAccessCountAndReturnUrl increments access count and returns URL")
    @Order(8)
    void incrementAccessCountAndReturnUrl_IncrementsAccessCountAndReturnsUrl_WhenSuccessful() {
        var url = utils.newSavedUrl();

        BDDMockito.when(repository.findUrlByShortCode(url.getShortCode())).thenReturn(Optional.of(url));

        var returnedUrl = service.incrementAccessCountAndReturnUrl(url.getShortCode());

        Assertions.assertThat(returnedUrl).isEqualTo(url.getUrl());
        Assertions.assertThat(url.getAccessCount()).isEqualTo(1); // verificando se foi incrementado
        BDDMockito.verify(repository).save(url);
    }

}