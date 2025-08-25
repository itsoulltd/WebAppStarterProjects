package com.infoworks.lab.controllers.jms;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@PropertySource("classpath:application-kafka.properties")
public class MessageListener {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @KafkaListener(topics = {"${topic.test}"}, concurrency = "1")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        //Retrieve the message content:
        String text = record.value();
        logger.log(Level.INFO, "Message received {0} ", text);
        ack.acknowledge();
    }

    @KafkaListener(topics = {"${topic.test}.DLT"}, concurrency = "1")
    public void listenerDLT(ConsumerRecord<String, String> record, Acknowledgment ack) {
        //Retrieve the message content:
        String text = record.value();
        logger.log(Level.INFO, "DLT Message received {0} ", text);
        ack.acknowledge();
    }

}
