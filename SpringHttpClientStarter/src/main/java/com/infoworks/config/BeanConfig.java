package com.infoworks.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infoworks.objects.MessageParser;
import com.infoworks.utils.services.iResources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfig {

    @Bean("HelloBean")
    public String getHello(){
        return "Hi Spring Hello";
    }

    @Bean
    public ObjectMapper getMapper(){
        //Solution: Add Jackson JSR-310 Module. Jackson doesn't know how to (de)serialize java.time.LocalDateTime,
        // because Java 8 time types are not supported out-of-the-box unless you register the JSR-310 module.
        ObjectMapper mapper = MessageParser.getJsonSerializer();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public iResources getResourceService(){
        return iResources.create();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder
            , @Value("${web.client.base-url}") String baseUrl
            , @Value("${web.client.username}") String username
            , @Value("${web.client.password}") String password
            , @Value("${web.client.in-memory.buffer.size.in-mb}") String inMemorySize) {
        WebClient webClient = builder.baseUrl(baseUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(Integer.parseInt(inMemorySize) * 1024 * 1024)) //inMemorySize in MB
                .build();
        return webClient;
    }

}
