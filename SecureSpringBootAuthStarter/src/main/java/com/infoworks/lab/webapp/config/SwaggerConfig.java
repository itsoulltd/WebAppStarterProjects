package com.infoworks.lab.webapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Value("${server.app.domain}")
    private String serverDomain;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.servlet.context-path}")
    private String servletContext;

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        OpenAPI apiDoc = new OpenAPI()
                .info(new Info()
                        .title("My REST API")
                        .version("1.0")
                        .description("Some custom description of API."))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME
                                , new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)));

        //To force HTTPS: e.g. "https://myapp.com/api/"
        apiDoc = apiDoc.servers(
                Arrays.asList(new Server().url(String.format("https://%s:%s%s/", serverDomain, serverPort, servletContext)))
        );

        return apiDoc;
    }
}
