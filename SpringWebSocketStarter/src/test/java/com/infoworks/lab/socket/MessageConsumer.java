package com.infoworks.lab.socket;

import com.infoworks.lab.client.spring.SocketTemplate;
import com.infoworks.lab.client.spring.SocketType;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.simulator.JsonLogWriter;

import java.util.concurrent.ExecutionException;

public class MessageConsumer extends com.infoworks.lab.simulator.Runtime {

    private JsonLogWriter writer = new JsonLogWriter(this.getClass(), "MessageProducer");

    @Override
    protected void run() {
        //writer.createIfNotExist(true);
        try {
            SocketTemplate socket = new SocketTemplate(SocketType.Standard);
            socket.setAuthorizationHeader("TOKEN");
            socket.setQueryParam("user_name", "user_name");
            socket.setQueryParam("secret", "app_secret");
            socket.connect("ws://localhost:8080/process");
            //
            socket.subscribe("/topic/event", Message.class, message -> {
                System.out.println(message.toString());
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
