package com.github.geovanegsfarias.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "URL Shortener API", version = "v1.0", description = "REST API for shortening URLs."))
public class OpenApiConfig {
}