package com.infoworks.lab.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = {
        "com.infoworks.lab.controllers"
        , "com.infoworks.lab.services"
        , "com.infoworks.lab.domain"
        , "com.infoworks.lab.webapp.config"})
public class AuthApi extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AuthApi.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AuthApi.class);
    }

}
