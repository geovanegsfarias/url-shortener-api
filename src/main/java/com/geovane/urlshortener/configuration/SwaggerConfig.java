package com.geovane.urlshortener.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("URL Shortener API")
                        .version("1.0")
                        .description("")
                        .contact(new Contact()
                                .name("Geovane")
                                .email("geovanegsfarias@gmail.com")));
    }
}