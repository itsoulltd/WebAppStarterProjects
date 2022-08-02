package com.infoworks.lab.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.infoworks.lab.batch"
        , "com.infoworks.lab.services"
        , "com.infoworks.lab.webapp.config"
        , "com.infoworks.lab.domain"})
public class BatchProcessingServicesApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(BatchProcessingServicesApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BatchProcessingServicesApplication.class);
    }
}
