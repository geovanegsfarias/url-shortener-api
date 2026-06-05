package com.github.geovanegsfarias.controller;

import com.github.geovanegsfarias.commons.FileUtils;
import com.github.geovanegsfarias.commons.UrlUtils;
import com.github.geovanegsfarias.mapper.UrlMapper;
import com.github.geovanegsfarias.repository.UrlRepository;
import com.github.geovanegsfarias.service.UrlService;
import org.junit.jupiter.api.*;
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
    @DisplayName("POST v1/shorten creates a url")
    @Order(2)
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
    @Order(3)
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
    @DisplayName("DELETE v1/shorten/1 removes a url")
    @Order(4)
    void deleteUrl_RemoveUrl_WhenSuccessful() throws Exception {
        var urlToDelete = urlUtils.newSavedUrl();
        var shortCode = urlToDelete.getShortCode();

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.of(urlToDelete));

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/shorten/{shortCode}", shortCode))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("GET v1/shorten/1/stats returns url stats")
    @Order(5)
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
    @DisplayName("GET v1/1 redirects to the original url")
    @Order(6)
    void redirectTo_redirectsToUrl_WhenSuccessful() throws Exception {
        var foundUrl = urlUtils.newSavedUrl();
        var shortCode = foundUrl.getShortCode();

        BDDMockito.when(repository.findUrlByShortCode(shortCode)).thenReturn(Optional.of(foundUrl));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{shortCode}", shortCode))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, foundUrl.getUrl()));
    }

}