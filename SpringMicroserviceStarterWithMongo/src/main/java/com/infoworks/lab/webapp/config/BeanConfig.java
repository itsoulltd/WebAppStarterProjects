package com.infoworks.lab.webapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean("HelloBean")
    public String getHello(){
        return "Hi Spring Hello";
    }

    @Bean
    ObjectMapper getMapper(){
        return Message.getJsonSerializer();
    }

}
