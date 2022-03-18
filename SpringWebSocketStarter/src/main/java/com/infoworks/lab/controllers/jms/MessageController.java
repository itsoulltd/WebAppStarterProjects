package com.infoworks.lab.controllers.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Queue;

@RestController
@RequestMapping("/v1/message")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    @Qualifier("testQueue")
    private Queue queue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @GetMapping("/send/{message}")
    public ResponseEntity<String> publish(@PathVariable("message") final String message){
        jmsTemplate.convertAndSend(queue, message);
        return new ResponseEntity(message, HttpStatus.OK);
    }

}
