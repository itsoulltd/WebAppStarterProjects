package com.infoworks.lab.webapp.config;

import com.infoworks.lab.util.services.iResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public iResourceService getResourceService(){
        return iResourceService.create();
    }

}
