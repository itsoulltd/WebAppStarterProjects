package com.infoworks.lab.webapp.config.jms;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

@Configuration
public class JmsQueueConfig {

    @Bean("testQueue")
    public Queue queue(){
        return new ActiveMQQueue("testQueue");
    }

}
