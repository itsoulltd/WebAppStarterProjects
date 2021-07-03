package com.infoworks.lab.webapp.config.socket.interceptor;

import com.infoworks.lab.webapp.config.socket.session.SessionAttributeKey;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class ByPassAuthorizationInterceptor extends WSocketAuthorizationInterceptor {

    public ByPassAuthorizationInterceptor() {}

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        //super.beforeHandshake(request, response, webSocketHandler, attributes);
        //Testing HttpSession:
        HttpSession session = ((ServletServerHttpRequest) request).getServletRequest().getSession();
        if (session != null) {
            attributes.put(SessionAttributeKey.SessionID.name(), session.getId());
            LOG.info("HttpSession ID: " + session.getId());
        }
        //By-Passing Security:
        return true;
    }
}
