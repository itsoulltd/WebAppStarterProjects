package com.infoworks.lab.controllers.jms;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/message")
public class MessageController {

    private KafkaTemplate<String, String> kafkaTemplate;

    public MessageController(@Qualifier("kafkaTextTemplate") KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${topic.test}")
    private String queue;

    @GetMapping("/send/{message}")
    public ResponseEntity<String> publish(@PathVariable("message") final String message){
        //
        kafkaTemplate.send(queue, message);
        return new ResponseEntity(message, HttpStatus.OK);
    }

}
