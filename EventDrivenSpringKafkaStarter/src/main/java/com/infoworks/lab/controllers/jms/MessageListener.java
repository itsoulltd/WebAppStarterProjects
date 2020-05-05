package com.infoworks.lab.controllers.jms;


import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@PropertySource("classpath:application.properties")
public class MessageListener {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private static int retryCount = 0;

    @KafkaListener(topics = {"${topic.test}"}, concurrency = "1")
    public void listener(@Payload String message) {
        // retrieve the message content
        String text = message;
        if (text.isEmpty() || text.startsWith("jms")) {
            if (retryCount == 3){ //on 3rd attempt
                retryCount = 0;
                logger.log(Level.INFO, "Now Handled DLQ {0} ", text);
            }else{
                retryCount++;
            }
        }else{
            //
            logger.log(Level.INFO, "Message received {0} ", text);
        }
    }

}
