package com.infoworks.lab.webapp;

import org.junit.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.infoworks.lab.controllers"
        , "com.infoworks.lab.services"})
public class BatchProcessingAppTest {
    @Test
    public void contextLoads() {
        System.out.println("ApplicationContext Loaded Successfully");
    }
}