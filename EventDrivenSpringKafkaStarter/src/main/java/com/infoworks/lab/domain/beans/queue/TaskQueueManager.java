package com.infoworks.lab.domain.beans.queue;

import com.infoworks.lab.beans.tasks.definition.QueuedTaskLifecycleListener;
import com.infoworks.lab.beans.queue.*;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TaskQueueManager extends AbstractTaskQueueManager {

    private static final Logger logger = Logger.getLogger("TaskQueueManager");

    public TaskQueueManager(@Autowired QueuedTaskLifecycleListener listener) {
        super(listener);
    }

    @KafkaListener(topics = {"${topic.execute}"}, concurrency = "5")
    public void startListener(@Payload String message, Acknowledgment ack) {
        // retrieve the message content
        String text = message;
        logger.log(Level.INFO, "EXE-QUEUE: Message received {0} ", text);
        if (handleTextOnStart(text)){
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = {"${topic.abort}"}, concurrency = "3")
    public void abortListener(@Payload String message, Acknowledgment ack) {
        // retrieve the message content
        String text = message;
        logger.log(Level.INFO, "ABORT-QUEUE: Message received {0} ", text);
        if (handleTextOnStop(text)){
            ack.acknowledge();
        }
    }

}
