package com.infoworks.lab.domain.beans.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.objects.Message;
import com.infoworks.tasks.Task;
import com.infoworks.tasks.queue.TaskQueue;
import com.infoworks.utils.jmsq.AbstractJmsQueue;
import com.infoworks.utils.jmsq.JmsMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

@Component("taskDispatchQueue")
public class TaskDispatchQueue extends AbstractJmsQueue {

    private Queue exeQueue;
    private Queue abortQueue;
    private JmsTemplate jmsTemplate;
    private ObjectMapper objectMapper;

    public TaskDispatchQueue(@Qualifier("exeQueue") Queue exeQueue
            , @Qualifier("abortQueue") Queue abortQueue
            , JmsTemplate jmsTemplate
            , ObjectMapper objectMapper) {
        this.exeQueue = exeQueue;
        this.abortQueue = abortQueue;
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public TaskQueue add(Task task) {
        //Defined:JmsMessage Protocol
        JmsMessage jmsMessage = convert(task);
        jmsTemplate.convertAndSend(exeQueue, jmsMessage.toString());
        return this;
    }

    @Override
    public void abort(Task task, Message error) {
        //Defined:JmsMessage Protocol
        JmsMessage jmsMessage = convert(task, error);
        jmsTemplate.convertAndSend(abortQueue, jmsMessage.toString());
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
