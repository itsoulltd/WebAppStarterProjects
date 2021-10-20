package com.infoworks.lab.domain.beans.queue;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.beans.queue.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component("taskDispatchQueue")
public class TaskDispatchQueue extends AbstractTaskQueue {

    @Autowired
    @Qualifier("kafkaTemplate")
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    @Qualifier("topic.execute")
    private String exeQueue;

    @Autowired
    @Qualifier("topic.abort")
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

}
