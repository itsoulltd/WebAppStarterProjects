package com.infoworks.lab.webapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        OpenAPI apiDoc = new OpenAPI()
                .info(new Info().title("My REST API")
                        .version("1.0")
                        .description("Some custom description of API."));
        return apiDoc;
    }
}
