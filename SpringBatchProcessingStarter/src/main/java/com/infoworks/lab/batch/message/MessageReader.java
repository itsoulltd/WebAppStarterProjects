package com.infoworks.lab.batch.message;

import com.infoworks.lab.rest.models.Message;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.concurrent.atomic.AtomicInteger;

public class MessageReader implements ItemReader<Message> {

    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Message read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        System.out.println("SimpleReader " + Thread.currentThread().getName());
        Message msg = new Message().setPayload(String.format("{\"message\":\"%s\"}", counter.getAndIncrement()));
        if (counter.get() >= 101){
            counter.set(0);
            return null; //Returning null marking the end of reading.
        } else {
            return msg;
        }
    }
}
