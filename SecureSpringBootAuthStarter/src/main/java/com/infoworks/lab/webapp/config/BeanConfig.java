package com.infoworks.lab.webapp.config;

import com.infoworks.utils.services.iResources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public iResources getResourceService(){
        return iResources.create();
    }

}
