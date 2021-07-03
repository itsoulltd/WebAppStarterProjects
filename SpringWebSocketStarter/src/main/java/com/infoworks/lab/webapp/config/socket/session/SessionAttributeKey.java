package com.infoworks.lab.webapp.config.socket.session;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public enum SessionAttributeKey {

    Token,
    TenantID,
    UserID,
    SessionID;

    public String value(SimpMessageHeaderAccessor accessor){
        return accessor.getSessionAttributes().get(this.name()).toString();
    }

}
