package com.github.geovanegsfarias.controller;

import com.github.geovanegsfarias.commons.FileUtils;
import com.github.geovanegsfarias.commons.UrlUtils;
import com.github.geovanegsfarias.mapper.UrlMapper;
import com.github.geovanegsfarias.repository.UrlRepository;
import com.github.geovanegsfarias.service.UrlService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.stream.Stream;

@WebMvcTest(controllers = UrlController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({UrlMapper.class, UrlService.class, UrlUtils.class, FileUtils.class})
class UrlControllerTest {
    private static final String URL = "/v1";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UrlRepository repository;
    @Autowired
    private UrlUtils urlUtils;
    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("GET v1/shorten/1 returns a url with given shortCode")
    @Order(1)
    void getUrl_returnsUrl_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("url/get-response-url-200.json");
        var foundUrl = urlUtils.newSavedUrl();
        var shortCode = foundUrl.getShortCode();

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.of(foundUrl));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + "/shorten/{shortCode}", shortCode)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/shorten/1 throws NotFound exception when url is not found")
    @Order(2)
    void getUrl_throwsNotFound_WhenUrlIsNotFound() throws Exception {
        var shortCode = "1";

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.empty());

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + "/shorten/{shortCode}", shortCode)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        var exceptionMessage = mvcResult.getResolvedException().getMessage();

        Assertions.assertThat(exceptionMessage).isNotNull();
        Assertions.assertThat(exceptionMessage).isEqualTo("URL not found");
    }

    @Test
    @DisplayName("POST v1/shorten creates a url")
    @Order(3)
    void saveUrl_CreatesUrl_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("url/post-request-url-200.json");
        var response = fileUtils.readResourceFile("url/post-response-url-201.json");

        var savedUrl = urlUtils.newSavedUrl();

        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(savedUrl);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL + "/shorten")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("PUT v1/shorten/1 updates a url")
    @Order(4)
    void updateUrl_updates_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("url/put-request-url-200.json");

        var urlToUpdate = urlUtils.newSavedUrl();
        var shortCode = urlToUpdate.getShortCode();

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.of(urlToUpdate));

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "/shorten/{shortCode}", shortCode)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("PUT v1/shorten/1 throws NotFound exception when url is not found")
    @Order(5)
    void updateUrl_throwsNotFound_WhenUrlIsNotFound() throws Exception {
        var request = fileUtils.readResourceFile("url/put-request-url-200.json");

        var shortCode = "1";

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.empty());

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "/shorten/{shortCode}", shortCode)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        var exceptionMessage = mvcResult.getResolvedException().getMessage();

        Assertions.assertThat(exceptionMessage).isNotNull();
        Assertions.assertThat(exceptionMessage).isEqualTo("URL not found");
    }

    @Test
    @DisplayName("DELETE v1/shorten/1 removes a url")
    @Order(6)
    void deleteUrl_RemoveUrl_WhenSuccessful() throws Exception {
        var urlToDelete = urlUtils.newSavedUrl();
        var shortCode = urlToDelete.getShortCode();

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.of(urlToDelete));

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/shorten/{shortCode}", shortCode))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE v1/shorten/1 throws NotFound exception when url is not found")
    @Order(7)
    void deleteUrl_ThrowsNotFound_WhenUrlIsNotFound() throws Exception {
        var shortCode = "1";

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.empty());

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/shorten/{shortCode}", shortCode))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        var exceptionMessage = mvcResult.getResolvedException().getMessage();

        Assertions.assertThat(exceptionMessage).isNotNull();
        Assertions.assertThat(exceptionMessage).isEqualTo("URL not found");
    }

    @Test
    @DisplayName("GET v1/shorten/1/stats returns url stats")
    @Order(8)
    void urlStats_returnStats_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("url/get-response-url-stats-200.json");

        var foundUrl = urlUtils.newSavedUrl();
        var shortCode = foundUrl.getShortCode();

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.of(foundUrl));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/shorten/{shortCode}/stats", shortCode))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/shorten/1/stats throws NotFound exception when url is not found")
    @Order(9)
    void urlStats_ThrowsNotFound_WhenUrlIsNotFound() throws Exception {
        var shortCode = "1";

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.empty());

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URL + "/shorten/{shortCode}/stats", shortCode))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        var exceptionMessage = mvcResult.getResolvedException().getMessage();

        Assertions.assertThat(exceptionMessage).isNotNull();
        Assertions.assertThat(exceptionMessage).isEqualTo("URL not found");
    }

    @Test
    @DisplayName("GET v1/1 redirects to the original url")
    @Order(10)
    void redirectTo_redirectsToUrl_WhenSuccessful() throws Exception {
        var foundUrl = urlUtils.newSavedUrl();
        var shortCode = foundUrl.getShortCode();

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.of(foundUrl));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{shortCode}", shortCode))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, foundUrl.getUrl()));
    }

    @Test
    @DisplayName("GET v1/1 throws NotFound exception when url is not found")
    @Order(11)
    void redirectTo_ThrowsNotFound_WhenUrlIsNotFound() throws Exception {
        var shortCode = "1";

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.empty());

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{shortCode}", shortCode))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        var exceptionMessage = mvcResult.getResolvedException().getMessage();

        Assertions.assertThat(exceptionMessage).isNotNull();
        Assertions.assertThat(exceptionMessage).isEqualTo("URL not found");
    }

    @ParameterizedTest
    @MethodSource("saveUrlBadRequestSource")
    @DisplayName("POST v1/shorten returns bad request when fields are not valid")
    @Order(12)
    void saveUrl_ReturnsBadRequest_WhenFieldsAreNotValid(String filePath, String errorMessage) throws Exception {
        var request = fileUtils.readResourceFile("%s".formatted(filePath));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post(URL + "/shorten")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        var exceptionMessage = mvcResult.getResolvedException().getMessage();

        Assertions.assertThat(exceptionMessage).isNotNull();
        Assertions.assertThat(exceptionMessage).contains(errorMessage);
    }

    @ParameterizedTest
    @MethodSource("updateUrlBadRequestSource")
    @DisplayName("PUT v1/shorten returns bad request when fields are not valid")
    @Order(13)
    void updateUrl_ReturnsBadRequest_WhenFieldsAreNotValid(String filePath, String errorMessage) throws Exception {
        var request = fileUtils.readResourceFile("%s".formatted(filePath));

        var shortCode = "1";

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "/shorten/{shortCode}", shortCode)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        var exceptionMessage = mvcResult.getResolvedException().getMessage();

        Assertions.assertThat(exceptionMessage).isNotNull();
        Assertions.assertThat(exceptionMessage).contains(errorMessage);
    }

    private static Stream<Arguments> saveUrlBadRequestSource() {
        return Stream.of(
                Arguments.of("url/post-request-url-blank-field-400.json", "URL must not be blank"),
                Arguments.of("url/post-request-url-empty-field-400.json", "URL must not be blank"),
                Arguments.of("url/post-request-url-invalid-field-400.json", "Invalid URL format")
        );
    }

    private static Stream<Arguments> updateUrlBadRequestSource() {
        return Stream.of(
                Arguments.of("url/put-request-url-blank-field-400.json", "URL must not be blank"),
                Arguments.of("url/put-request-url-empty-field-400.json", "URL must not be blank"),
                Arguments.of("url/put-request-url-invalid-field-400.json", "Invalid URL format")
        );
    }

}