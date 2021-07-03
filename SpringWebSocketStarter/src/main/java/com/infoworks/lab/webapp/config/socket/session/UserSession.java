package com.infoworks.lab.webapp.config.socket.session;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.Map;
import java.util.Optional;

public class UserSession {

    private static final String USER_ID = SessionAttributeKey.UserID.name();
    private static final String TENANT_ID = SessionAttributeKey.TenantID.name();
    private static final String TOKEN = SessionAttributeKey.Token.name();
    private static final String SESSION_ID = SessionAttributeKey.SessionID.name();

    public static Optional<UserSession> create(Map<String, Object> accessor){
        //Token must not be null
        Optional<String> token = Optional.ofNullable((String)accessor.get(TOKEN));
        if (token.isPresent()){
            UserSession usession = new UserSession(token.get());
            //Tenant must not be null
            Optional<String> tenantID = Optional.ofNullable((String)accessor.get(TENANT_ID));
            if (tenantID.isPresent()) usession.setTenantID(tenantID.get());
            //UserID
            Optional<String> userID = Optional.ofNullable((String)accessor.get(USER_ID));
            usession.setUserID(userID);
            //SessionID
            Optional<String> sessionID = Optional.ofNullable((String)accessor.get(SESSION_ID));
            usession.setSessionID(sessionID);
            //
            return Optional.ofNullable(usession);
        }
        return Optional.ofNullable(null);
    }

    public static Optional<UserSession> retrieve(SimpMessageHeaderAccessor accessor){
        //Token must not be null
        Map<String, Object> attributes = accessor.getSessionAttributes();
        if (attributes != null) {
            Optional<String> token = Optional.ofNullable((String)attributes.get(TOKEN));
            if (token.isPresent()) {
                Optional<UserSession> session = create(attributes);
                return session;
            }
        }
        return Optional.ofNullable(null);
    }

    private UserSession() {/*EMPTY*/}

    private Optional<String> username = Optional.ofNullable(null);
    private Optional<String> userID = Optional.ofNullable(null);
    private Optional<String> sessionID = Optional.ofNullable(null);
    private String tenantID;
    private String token;

    private UserSession(String token) {
        this.token = token;
    }

    public Optional<String> getUserID() {
        return userID;
    }

    public void setUserID(Optional<String> userID) {
        if (this.userID.isPresent()) return;
        this.userID = userID;
    }

    public String getTenantID() {
        return tenantID;
    }

    private void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    public String getToken() {
        return token;
    }

    private void setToken(String token) {
        this.token = token;
    }

    public Optional<String> getUsername() {
        return username.isPresent() ? username : userID;
    }

    public void setUsername(Optional<String> username) {
        if (this.username.isPresent()) return;
        this.username = username;
    }

    public Optional<String> getSessionID() {
        return sessionID;
    }

    public void setSessionID(Optional<String> sessionID) {
        if (this.sessionID.isPresent()) return;
        this.sessionID = sessionID;
    }
}
