package com.infoworks.lab.controllers.ws;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.events.Event;
import com.infoworks.lab.rest.models.events.EventType;
import com.infoworks.lab.webapp.config.socket.session.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Controller
public class MessagingController {

    private static Logger LOG = Logger.getLogger(MessagingController.class.getSimpleName());

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * How to Respond on Client's Subscription:
     * @param sessionID
     * @param accessor
     * @return
     */
    @SubscribeMapping("/initialize")
    public Message<Event> onSubscribeConsumingEvent(
            @Header("simpSessionId") String sessionID
            , SimpMessageHeaderAccessor accessor){
        //
        Message<Event> message = new Message<>();
        Event ev = new Event();
        ev.setEventType(EventType.ACTIVATE);
        //
        Optional<UserSession> session = UserSession.retrieve(accessor);
        if (session.isPresent()){
            UserSession us = session.get();
            JsonObject obj = Json.createObjectBuilder()
                    .add("message","Subscription Initiation Message From Server!")
                    .add("simpSessionID", sessionID)
                    .add("userID", us.getUserID().get())
                    .build();
            message.setPayload(obj.toString());
        }
        message.setEvent(ev);
        return message;
    }

    @MessageMapping("/event")
    public void handleEvent(@Payload Message<Event> message){
        LOG.info(message.toString());
        messagingTemplate.convertAndSend("/topic/event",message);
    }

    /**
     * Example of how to asynchronously send message to a user session (simpSessionId):
     * @param sessionID
     */
    @MessageMapping("/event/async/process")
    public void handleLongRunningProcess(@Header("simpSessionId") String sessionID){

        //Simple try with SimpSessionID:
        final String ssID = sessionID;

        //Start a long running job
        exeService.submit(() -> {
            System.out.println("Job Started for " + ssID);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message<Event> message = new Message<>();
            Event ev = new Event();
            message.setPayload("This From Long Running Async-Job!");
            message.setEvent(ev);
            //Following Become -> /user/<session-id>/queue/async/event
            //And on client side, we subscribe on topic:"/user/queue/async/event"
            messagingTemplate.convertAndSendToUser(ssID
                    , "/queue/async/event"
                    , message
                    , createHeaders(ssID));
            //
            System.out.println("Job Ended for " + ssID);
        });
    }

    private ExecutorService exeService = Executors.newSingleThreadExecutor();

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

}
