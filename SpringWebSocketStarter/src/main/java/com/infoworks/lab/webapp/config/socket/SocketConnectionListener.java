package com.infoworks.lab.webapp.config.socket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.logging.Logger;

@Component
public class SocketConnectionListener {

    private Logger log = Logger.getLogger(this.getClass().getName());

    @EventListener
    public void socketDidConnected(SessionConnectedEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("Received Web Socket connection -> SessionID: " + headerAccessor.getSessionId());
    }

    @EventListener
    public void socketDidDisconnect(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("Socket Disconnected -> SessionID: " + headerAccessor.getSessionId());
    }

}
