package com.github.geovanegsfarias.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@Profile("itest")
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?>postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17")).withDatabaseName("url_shortener_test_db");
    }
}
