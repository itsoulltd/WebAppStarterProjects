package com.infoworks.lab.controllers.jms;


import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@PropertySource("classpath:application.properties")
@PropertySource("classpath:kafka.properties")
public class MessageListener {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @KafkaListener(topics = {"${topic.test}"}, concurrency = "1")
    public void listener(@Payload String message, Acknowledgment ack) {
        //Retrieve the message content:
        String text = message;
        logger.log(Level.INFO, "Message received {0} ", text);
        ack.acknowledge();
    }

}
