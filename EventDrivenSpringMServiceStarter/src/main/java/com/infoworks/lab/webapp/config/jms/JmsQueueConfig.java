package com.infoworks.lab.webapp.config.jms;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.jms.Queue;

@Configuration
@PropertySource("classpath:jms.properties")
public class JmsQueueConfig {

    public Environment env;

    public JmsQueueConfig(Environment env) {
        this.env = env;
    }

    @Bean("testQueue")
    public Queue queue(){
        return new ActiveMQQueue(env.getProperty("jms.queue.test"));
    }

    @Bean("exeQueue")
    public Queue getExeQueue(){
        return new ActiveMQQueue(env.getProperty("jms.queue.exe"));
    }

    @Bean("abortQueue")
    public Queue getAbortQueue(){
        return new ActiveMQQueue(env.getProperty("jms.queue.abort"));
    }

}
