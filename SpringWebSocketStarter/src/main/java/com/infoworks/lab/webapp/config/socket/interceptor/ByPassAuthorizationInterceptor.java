package com.infoworks.lab.webapp.config.socket.interceptor;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.webapp.config.socket.session.SessionAttributeKey;
import com.infoworks.lab.webapp.config.socket.session.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class ByPassAuthorizationInterceptor extends WSocketAuthorizationInterceptor {

    private static Logger LOG = LoggerFactory.getLogger(ByPassAuthorizationInterceptor.class.getSimpleName());

    public ByPassAuthorizationInterceptor() {}

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        LOG.info("beforeHandshake: Called");
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        //Testing HttpSession:
        HttpSession session = ((ServletServerHttpRequest) request).getServletRequest().getSession();
        if (session != null) {
            attributes.put(SessionAttributeKey.SessionID.name(), session.getId());
        }
        //By-Passing Security:
        MultiValueMap<String, String> parameters = getQueryParams(servletRequest);
        LOG.info(Message.marshal(parameters));
        parameters.forEach((key, value) -> {
            if (key.trim().equalsIgnoreCase(HttpHeaders.AUTHORIZATION))
                attributes.put(SessionAttributeKey.Token.name(), parameters.getFirst(key));
            else if (key.trim().equalsIgnoreCase(SessionAttributeKey.UserID.name()))
                attributes.put(SessionAttributeKey.UserID.name(), parameters.getFirst(key));
            else if (key.trim().equalsIgnoreCase(SessionAttributeKey.TenantID.name()))
                attributes.put(SessionAttributeKey.TenantID.name(), parameters.getFirst(key));
            else
                attributes.put(key, parameters.getFirst(key));
        });
        //Create-User-Session:
        LOG.info(Message.marshal(attributes));
        UserSession.create(attributes);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        LOG.info("afterHandshake: Called");
    }
}
