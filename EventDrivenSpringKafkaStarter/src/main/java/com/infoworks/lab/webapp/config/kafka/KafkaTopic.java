package com.infoworks.lab.webapp.config.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class KafkaTopic {

    @Autowired
    Environment env;

    @Bean("topic.execute")
    public String execute(){
        return env.getProperty("topic.execute");
    }

    @Bean("topic.abort")
    public String abort(){
        return env.getProperty("topic.abort");
    }

    @Bean("topic.test")
    public String test(){
        return env.getProperty("topic.test");
    }
}
