package com.infoworks.lab.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Base spring boot init class
 * SHOULD always BE in the TOP LEVEL, not in any package,
 * because vaadin don't recognize any route which is not in the child / same dir of the Application class
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
