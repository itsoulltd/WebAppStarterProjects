package com.lab.infoworks.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = {
        "com.lab.infoworks.controllers"
        , "com.lab.infoworks.services"
        , "com.lab.infoworks.domain"
        , "com.lab.infoworks.webapp.config"})
public class AuthApi extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AuthApi.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AuthApi.class);
    }

}
