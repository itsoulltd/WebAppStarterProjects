package com.infoworks.lab.webapp.config.jms;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SimpleJmsTemplate implements AutoCloseable{

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SimpleJmsTemplate.class);

    private Connection connection;
    private Session session;
    private final Map<String, MessageConsumer> messageConsumers;
    private final Map<String, MessageProducer> messageProducers;

    private String brokerURL;
    private String username;
    private String password;

    public SimpleJmsTemplate(String brokerURL) {
        messageConsumers = new ConcurrentHashMap<>();
        messageProducers = new ConcurrentHashMap<>();
        this.brokerURL = brokerURL;
    }

    public synchronized void createSession(String username, String password) {
        this.username = username;
        this.password = password;
        try {
            // create a Connection Factory
            if (brokerURL == null || brokerURL.isEmpty())
                brokerURL = ActiveMQConnection.DEFAULT_BROKER_URL;
            //
            ConnectionFactory connectionFactory =
                    new ActiveMQConnectionFactory(username, password, brokerURL);
            // create a Connection
            connection = connectionFactory.createConnection();
            // create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // start the connection in order to receive messages
            connection.start();
        } catch (JMSException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public synchronized void close() {
        try {
            //close-up: producers
            messageProducers.values().stream().forEach(messageProducer -> {
                try {
                    messageProducer.close();
                } catch (JMSException e) {LOG.error(e.getMessage(), e);}
            });
            messageProducers.clear();
            //close-up: consumers
            messageConsumers.values().stream().forEach(messageConsumer -> {
                try {
                    messageConsumer.close();
                } catch (JMSException e) {LOG.error(e.getMessage(), e);}
            });
            messageConsumers.clear();
            //close-up: session
            if (session != null){
                try {
                    session.close();
                } catch (JMSException e) {LOG.error(e.getMessage(), e);}
                finally {
                    session = null;
                }
            }
            //close-up: connection
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) { LOG.error(e.getMessage(), e); }
        finally {
            connection = null;
        }
    }

    public Message receiveTextMessage(String destination, int timeout) throws JMSException{
        // read a message from the queue destination
        createQueue(destination);
        MessageConsumer messageConsumer = messageConsumers.get(destination);
        return messageConsumer.receive(timeout);
    }

    public void receiveTextMessage(String destination, Consumer<Message> consumer) throws JMSException{
        // read a message from the queue destination
        createQueue(destination);
        MessageConsumer messageConsumer = messageConsumers.get(destination);
        messageConsumer.setMessageListener(message -> {
            if (consumer != null)
                consumer.accept(message);
        });
    }

    public void convertAndSend(String destination, String text) throws JMSException{
        // create a JMS TextMessage
        TextMessage textMessage = session.createTextMessage(text);
        // send the message to the queue destination
        createQueue(destination);
        MessageProducer messageProducer = messageProducers.get(destination);
        messageProducer.send(textMessage);
    }

    private void createQueue(String destinationName) throws JMSException {
        if (messageConsumers.containsKey(destinationName) && messageProducers.containsKey(destinationName)){
            return;
        }
        // create the Destination from which messages will be received
        if (session == null || connection == null){
            close();
            createSession(this.username, this.password);
        }
        Destination destination = session.createQueue(destinationName);
        // create a Message Producer for sending messages
        if (!messageProducers.containsKey(destinationName)){
            MessageProducer messageProducer = session.createProducer(destination);
            messageProducers.put(destinationName, messageProducer);
        }
        // create a Message Consumer for receiving messages
        if (!messageConsumers.containsKey(destinationName)){
            MessageConsumer messageConsumer = session.createConsumer(destination);
            messageConsumers.put(destinationName, messageConsumer);
        }
    }

}
