package com.infoworks.lab.webapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.rest.models.Message;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean("HelloBean")
    public String getHello(){
        return "Hi Spring Hello";
    }

    @Bean("passengerDatasource")
    public SimpleDataSource<String, Passenger> getPassengerDatasource(){
        return new SimpleDataSource<>();
    }

    @Bean
    ObjectMapper getMapper(){
        return Message.getJsonSerializer();
    }

}
