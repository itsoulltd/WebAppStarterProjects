package com.infoworks.lab.batch.message;

import com.infoworks.lab.rest.models.Message;
import org.springframework.batch.item.ItemProcessor;

public class MessageProcessor implements ItemProcessor<Message, Message> {
    @Override
    public Message process(Message o) throws Exception {
        System.out.println("SimpleProcessor " + Thread.currentThread().getName());
        //o.setPayload("{\"message\":\"hello there! processed!\"}");
        return o;
    }
}
