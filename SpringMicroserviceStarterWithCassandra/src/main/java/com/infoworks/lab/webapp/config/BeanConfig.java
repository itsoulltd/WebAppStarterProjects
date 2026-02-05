package com.infoworks.lab.webapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.objects.MessageParser;
import com.infoworks.utils.services.iResources;
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
        return MessageParser.getJsonSerializer();
    }

    @Bean
    public iResources getResourceService(){
        return iResources.create();
    }

}
