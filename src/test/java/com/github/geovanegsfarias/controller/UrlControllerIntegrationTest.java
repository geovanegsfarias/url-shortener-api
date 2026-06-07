package com.github.geovanegsfarias.controller;

import com.github.geovanegsfarias.commons.FileUtils;
import com.github.geovanegsfarias.configuration.TestcontainersConfig;
import com.github.geovanegsfarias.exception.UrlNotFoundException;
import com.github.geovanegsfarias.repository.UrlRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("itest")
class UrlControllerIntegrationTest {
    private static final String URL = "/v1";
    @LocalServerPort
    int port;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private UrlRepository repository;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost:%s".formatted(port);
    }

    @Test
    @DisplayName("GET v1/shorten/1 returns a url with given shortCode")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(1)
    void getUrl_returnsUrl_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("url/get-response-url-200.json");
        var shortCode = "1";

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/shorten/{shortCode}", shortCode)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("createdAt", "updatedAt")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET v1/shorten/1 throws NotFound exception when url is not found")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(2)
    void getUrl_throwsNotFound_WhenUrlIsNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("url/get-response-url-404.json");

        var shortCode = "99";

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/shorten/{shortCode}", shortCode)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("POST v1/shorten creates a url")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(3)
    void saveUrl_CreatesUrl_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("url/post-request-url-200.json");
        var expectedResponse = fileUtils.readResourceFile("url/post-response-url-201.json");

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/shorten")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .node("id")
                .asNumber()
                .isPositive();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id", "shortCode", "createdAt", "updatedAt")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("PUT v1/shorten/1 updates a url")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(4)
    void updateUrl_updates_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("url/put-request-url-200.json");

        var shortCode = "1";

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL + "/shorten/{shortCode}", shortCode)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        var updatedUser = repository.findUrlByShortCode(shortCode).orElseThrow(() -> new UrlNotFoundException("URL not found"));
        Assertions.assertThat(updatedUser.getUrl()).isEqualTo("https://www.youtube.com/");
    }

    @Test
    @DisplayName("PUT v1/shorten/1 throws NotFound exception when url is not found")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(5)
    void updateUrl_throwsNotFound_WhenUrlIsNotFound() throws Exception {
        var request = fileUtils.readResourceFile("url/put-request-url-200.json");
        var expectedResponse = fileUtils.readResourceFile("url/put-response-url-404.json");

        var shortCode = "99";

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL + "/shorten/{shortCode}", shortCode)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("DELETE v1/shorten/1 removes a url")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(6)
    void deleteUrl_RemoveUrl_WhenSuccessful() {
        var shortCode = "1";

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .delete(URL + "/shorten/{shortCode}", shortCode)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        Assertions.assertThat(repository.findUrlByShortCode(shortCode)).isEmpty();
    }

    @Test
    @DisplayName("DELETE v1/shorten/1 throws NotFound exception when url is not found")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(7)
    void deleteUrl_ThrowsNotFound_WhenUrlIsNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("url/delete-response-url-404.json");

        var shortCode = "99";

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .delete(URL + "/shorten/{shortCode}", shortCode)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET v1/shorten/1/stats returns url stats")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(8)
    void urlStats_returnStats_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("url/get-response-url-stats-200.json");

        var shortCode = "1";

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/shorten/{shortCode}/stats", shortCode)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id", "createdAt", "updatedAt")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET v1/shorten/1/stats throws NotFound exception when url is not found")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(9)
    void urlStats_ThrowsNotFound_WhenUrlIsNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("url/get-response-url-stats-404.json");

        var shortCode = "99";

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/shorten/{shortCode}/stats", shortCode)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET v1/1 redirects to the original url")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(10)
    void redirectTo_redirectsToUrl_WhenSuccessful() throws Exception {
        var shortCode = "1";

        var savedUrl = repository.findUrlByShortCode(shortCode).orElseThrow(() -> new UrlNotFoundException("URL not found"));

        RestAssured.given()
                .redirects().follow(false)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/{shortCode}", shortCode)
                .then()
                .statusCode(HttpStatus.FOUND.value())
                .header(HttpHeaders.LOCATION, savedUrl.getUrl())
                .log().all();

        var url = repository.findUrlByShortCode(shortCode).orElseThrow(() -> new UrlNotFoundException("URL not found"));

        Assertions.assertThat(url.getAccessCount()).isEqualTo(1); // verificando se foi incrementado
    }

    @Test
    @DisplayName("GET v1/1 throws NotFound exception when url is not found")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(11)
    void redirectTo_ThrowsNotFound_WhenUrlIsNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("url/get-response-url-redirect-404.json");

        var shortCode = "99";

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/{shortCode}", shortCode)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @MethodSource("saveUrlBadRequestSource")
    @DisplayName("POST v1/shorten returns bad request when fields are not valid")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(12)
    void saveUrl_ReturnsBadRequest_WhenFieldsAreNotValid(String requestFile, String responseFile) throws Exception {
        var request = fileUtils.readResourceFile("%s".formatted(requestFile));
        var expectedResponse = fileUtils.readResourceFile("%s".formatted(responseFile));

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/shorten")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @MethodSource("updateUrlBadRequestSource")
    @DisplayName("PUT v1/shorten returns bad request when fields are not valid")
    @Sql(value = "/sql/init_three_urls.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_urls.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(13)
    void updateUrl_ReturnsBadRequest_WhenFieldsAreNotValid(String requestFile, String responseFile) throws Exception {
        var request = fileUtils.readResourceFile("%s".formatted(requestFile));
        var expectedResponse = fileUtils.readResourceFile("%s".formatted(responseFile));

        var shortCode = "1";

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL + "/shorten/{shortCode}", shortCode)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);
    }

    private static Stream<Arguments> saveUrlBadRequestSource() {
        return Stream.of(
                Arguments.of("url/post-request-url-blank-field-400.json", "url/post-response-url-blank-field-400.json"),
                Arguments.of("url/post-request-url-empty-field-400.json", "url/post-response-url-empty-field-400.json"),
                Arguments.of("url/post-request-url-invalid-field-400.json", "url/post-response-url-invalid-field-400.json")
        );
    }

    private static Stream<Arguments> updateUrlBadRequestSource() {
        return Stream.of(
                Arguments.of("url/put-request-url-blank-field-400.json", "url/put-response-url-blank-field-400.json"),
                Arguments.of("url/put-request-url-empty-field-400.json", "url/put-response-url-empty-field-400.json"),
                Arguments.of("url/put-request-url-invalid-field-400.json", "url/put-response-url-invalid-field-400.json")
        );
    }

}
