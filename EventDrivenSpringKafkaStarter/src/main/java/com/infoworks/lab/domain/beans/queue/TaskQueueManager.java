package com.infoworks.lab.domain.beans.queue;

import com.infoworks.lab.beans.tasks.definition.QueuedTaskLifecycleListener;
import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.impl.AbstractQueueManager;
import com.infoworks.lab.rest.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@PropertySource("classpath:application.properties")
public class TaskQueueManager extends AbstractQueueManager {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private QueuedTaskLifecycleListener listener;

    public TaskQueueManager(@Autowired QueuedTaskLifecycleListener listener) {
        this.listener = listener;
    }

    @KafkaListener(topics = {"${topic.execute}"}, concurrency = "5")
    public void startlistener(@Payload String message) {
        // retrieve the message content
        String text = message;
        logger.log(Level.INFO, "EXE-QUEUE: Message received {0} ", text);
        try {
            //Defined:JmsMessage Protocol
            JmsMessage jmsMessage = Message.unmarshal(JmsMessage.class, text);
            Task task = (Task) Class.forName(jmsMessage.getTaskClassName()).newInstance();
            Class<? extends Message> messageClass = (Class<? extends Message>) Class.forName(jmsMessage.getMessageClassName());
            Message taskMessage = Message.unmarshal(messageClass, jmsMessage.getPayload());
            task.setMessage(taskMessage);
            //
            start(task, null);
            //
        }catch (RuntimeException | IOException
                | ClassNotFoundException
                | IllegalAccessException | InstantiationException e){
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @KafkaListener(topics = {"${topic.abort}"}, concurrency = "3")
    public void abortListener(@Payload String message) {
        // retrieve the message content
        String text = message;
        logger.log(Level.INFO, "ABORT-QUEUE: Message received {0} ", text);
        try {
            //Defined:JmsMessage Protocol
            JmsMessage jmsMessage = Message.unmarshal(JmsMessage.class, text);
            Task task = (Task) Class.forName(jmsMessage.getTaskClassName()).newInstance();
            Class<? extends Message> messageClass = (Class<? extends Message>) Class.forName(jmsMessage.getMessageClassName());
            Message taskMessage = Message.unmarshal(messageClass, jmsMessage.getPayload());
            task.setMessage(taskMessage);
            //Handle error-message:
            Class<? extends Message> errorClass = (Class<? extends Message>) Class.forName(jmsMessage.getErrorClassName());
            Message errorMessage = Message.unmarshal(errorClass, jmsMessage.getErrorPayload());
            //
            stop(task, errorMessage);
            //
        }catch (RuntimeException | IOException
                | ClassNotFoundException
                | IllegalAccessException | InstantiationException e){
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public void terminateRunningTasks(long l, TimeUnit timeUnit) {
        //TODO:
        //send termination to jms-template for stopping current processing or abandon all active task from
        // exeQueue:
    }

    @Override
    public void close() throws Exception {
        //TODO:
        //Clean of any resource:
    }

    public QueuedTaskLifecycleListener getListener() {
        return listener;
    }

    @Override
    public void setListener(QueuedTaskLifecycleListener queuedTaskLifecycleListener) {
        this.listener = queuedTaskLifecycleListener;
    }

}
