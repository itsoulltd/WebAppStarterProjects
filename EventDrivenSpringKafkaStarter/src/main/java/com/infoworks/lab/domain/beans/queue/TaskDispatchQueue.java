package com.infoworks.lab.domain.beans.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.objects.Message;
import com.infoworks.tasks.Task;
import com.infoworks.tasks.queue.TaskQueue;
import com.infoworks.utils.jmsq.AbstractJmsQueue;
import com.infoworks.utils.jmsq.JmsMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component("taskDispatchQueue")
public class TaskDispatchQueue extends AbstractJmsQueue {

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    public TaskDispatchQueue(@Qualifier("kafkaTextTemplate") KafkaTemplate kafkaTemplate
            , ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Value("${topic.execute}")
    private String exeQueue;

    @Value("${topic.abort}")
    private String abortQueue;

    @Override
    public TaskQueue add(Task task) {
        //Defined:JmsMessage Protocol
        JmsMessage jmsMessage = convert(task);
        kafkaTemplate.send(exeQueue, jmsMessage.toString());
        return this;
    }

    @Override
    public void abort(Task task, Message error) {
        //Defined:JmsMessage Protocol
        JmsMessage jmsMessage = convert(task, error);
        kafkaTemplate.send(abortQueue, jmsMessage.toString());
    }

    @Override
    public TaskQueue cancel(Task task) {
        //TODO:
        return this;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

}
