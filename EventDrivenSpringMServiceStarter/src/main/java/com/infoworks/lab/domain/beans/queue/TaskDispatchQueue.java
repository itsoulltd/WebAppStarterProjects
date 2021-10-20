package com.infoworks.lab.domain.beans.queue;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.beans.queue.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

@Component("taskDispatchQueue")
public class TaskDispatchQueue extends AbstractTaskQueue {

    @Autowired
    @Qualifier("exeQueue")
    private Queue exeQueue;

    @Autowired
    @Qualifier("abortQueue")
    private Queue abortQueue;

    @Autowired
    private JmsTemplate jmsTemplate;

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

}
