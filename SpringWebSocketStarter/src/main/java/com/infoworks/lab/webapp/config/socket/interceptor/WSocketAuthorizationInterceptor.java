package com.infoworks.lab.webapp.config.socket.interceptor;

import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.webapp.config.JWTokenValidator;
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
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class WSocketAuthorizationInterceptor implements HandshakeInterceptor {

    private static Logger LOG = LoggerFactory.getLogger(WSocketAuthorizationInterceptor.class.getSimpleName());

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        LOG.info("beforeHandshake: Called");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            //Security Check here:
            MultiValueMap<String, String> parameters = getQueryParams(servletRequest);
            LOG.info(Message.marshal(parameters));
            //
            if (parameters.containsKey(HttpHeaders.AUTHORIZATION)){
                HttpSession session = servletRequest.getServletRequest().getSession();
                if (session != null) {
                    attributes.put(SessionAttributeKey.SessionID.name(), session.getId());
                }
                //TODO: Map additional header parameter from client:
                parameters.forEach((key, value) -> {
                    if (key.trim().equalsIgnoreCase(SessionAttributeKey.UserID.name()))
                        attributes.put(SessionAttributeKey.UserID.name(), parameters.getFirst(key));
                    else if (key.trim().equalsIgnoreCase(SessionAttributeKey.TenantID.name()))
                        attributes.put(SessionAttributeKey.TenantID.name(), parameters.getFirst(key));
                    else
                        attributes.put(key, parameters.getFirst(key));
                });
                //Fetch Tenant's Secret and Validate the JWT Token.
                String token = parameters.getFirst(HttpHeaders.AUTHORIZATION);
                attributes.put(SessionAttributeKey.Token.name(), token);
                //
                JWTValidator validator = new JWTokenValidator(servletRequest.getServletRequest());
                boolean isValid = validator.isValid(token);
                if (isValid) {
                    LOG.info(Message.marshal(attributes));
                    UserSession.create(attributes);
                }
                return isValid;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        LOG.info("afterHandshake: Called");
    }

    protected MultiValueMap<String, String> getQueryParams(ServletServerHttpRequest servletRequest) {
        LOG.info(servletRequest.getURI().toString());
        MultiValueMap<String, String> parameters =
                UriComponentsBuilder.fromUriString(servletRequest.getURI().toString()).build().getQueryParams();
        return parameters;
    }

    protected Map<String, String> getRequestHeaders(ServletServerHttpRequest servletRequest){
        Map<String, String> map = new HashMap<>();
        MultiValueMap<String, String> headers = servletRequest.getHeaders();
        headers.forEach((key, items) ->{
            if (items.size() > 0){
                if (key.trim().equalsIgnoreCase(HttpHeaders.AUTHORIZATION)){
                    String authorization = items.get(0);
                    map.put(HttpHeaders.AUTHORIZATION, authorization);
                }
            }
        });
        return map;
    }
}
