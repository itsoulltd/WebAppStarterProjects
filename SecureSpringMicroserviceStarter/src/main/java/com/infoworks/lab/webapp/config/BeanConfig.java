package com.infoworks.lab.webapp.config;

import com.infoworks.lab.util.services.iResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean("HelloBean")
    public String getHello(){
        return "Hi Spring Hello";
    }

    @Bean
    public iResourceService getResourceService(){
        return iResourceService.create();
    }

}
