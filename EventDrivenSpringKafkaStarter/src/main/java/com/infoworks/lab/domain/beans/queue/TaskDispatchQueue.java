package com.infoworks.lab.domain.beans.queue;

import com.infoworks.lab.beans.tasks.definition.*;
import com.infoworks.lab.rest.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component("taskDispatchQueue")
public class TaskDispatchQueue implements TaskQueue, QueuedTaskLifecycleListener {

    private BiConsumer<Message, TaskStack.State> callback;
    private TaskCompletionListener listener;

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
        JmsMessage jmsMessage = new JmsMessage()
                .setTaskClassName(task.getClass().getName())
                .setMessageClassName(Message.class.getName());
        if (task.getMessage() != null) {
            jmsMessage.setMessageClassName(task.getMessage().getClass().getName())
                    .setPayload(task.getMessage().toString());
        }
        kafkaTemplate.send(exeQueue, jmsMessage.toString());
        return this;
    }

    @Override
    public void abort(Task task, Message error) {
        //Defined:JmsMessage Protocol
        JmsMessage jmsMessage = new JmsMessage()
                .setTaskClassName(task.getClass().getName())
                .setMessageClassName(Message.class.getName())
                .setErrorClassName(Message.class.getName());
        if (task.getMessage() != null) {
            jmsMessage.setMessageClassName(task.getMessage().getClass().getName())
                    .setPayload(task.getMessage().toString());
        }
        if (error != null){
            jmsMessage.setErrorClassName(error.getClass().getName())
                    .setErrorPayload(error.toString());
        }
        kafkaTemplate.send(abortQueue, jmsMessage.toString());
    }

    @Override
    public TaskQueue cancel(Task task) {
        //TODO:
        return this;
    }

    @Override
    public void onTaskComplete(BiConsumer<Message, TaskStack.State> biConsumer) {
        this.callback = biConsumer;
    }

    @Override
    public void onTaskComplete(TaskCompletionListener taskCompletionListener) {
        this.listener = taskCompletionListener;
    }

    @Override
    public void failed(Message message) {
        try {
            if (callback != null){
                callback.accept(message, TaskStack.State.Failed);
            }else if (listener != null){
                listener.failed(message);
            }
        } catch (Exception e) {}
    }

    @Override
    public void finished(Message message) {
        try {
            if (callback != null){
                callback.accept(message, TaskStack.State.Finished);
            }else if (listener != null){
                listener.finished(message);
            }
        } catch (Exception e) {}
    }

}
