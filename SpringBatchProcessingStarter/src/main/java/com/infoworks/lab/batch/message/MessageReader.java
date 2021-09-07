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
        System.out.println("SimpleReader");
        Message msg = new Message().setPayload(String.format("{\"message\":\"%s\"}", counter.getAndIncrement()));
        return counter.get() >= 101 ? null : msg; //Returning null marking the end of reading.
    }
}
