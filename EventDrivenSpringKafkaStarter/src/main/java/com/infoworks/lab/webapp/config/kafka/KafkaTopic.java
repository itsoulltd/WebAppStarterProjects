package com.infoworks.lab.webapp.config.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopic {

    @Bean("topic.execute")
    private String execute(){
        return "topic.execute";
    }

    @Bean("topic.abort")
    private String abort(){
        return "topic.abort";
    }

    @Bean("topic.test")
    private String test(){
        return "topic.test";
    }
}
