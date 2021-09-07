package com.infoworks.lab.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.infoworks.lab.batch"
        , "com.infoworks.lab.webapp.config"
        , "com.infoworks.lab.domain"})
public class BatchProcessingApp extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(BatchProcessingApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BatchProcessingApp.class);
    }
}
