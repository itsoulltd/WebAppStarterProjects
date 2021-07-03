package com.infoworks.lab.webapp.config.socket.interceptor;

import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.webapp.config.JWTokenValidator;
import com.infoworks.lab.webapp.config.socket.session.SessionAttributeKey;
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

    protected static Logger LOG = LoggerFactory.getLogger(WSocketAuthorizationInterceptor.class.getSimpleName());

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        //TODO:
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            //
            ServletServerHttpRequest servletRequest
                    = (ServletServerHttpRequest) request;

            //Security Check here:
            MultiValueMap<String, String> parameters = getAuthParameters(servletRequest);
            //
            if (parameters.containsKey(HttpHeaders.AUTHORIZATION)){

                //Fetch Tenant's Secret and Validate the JWT Token.
                String token = parameters.getFirst(HttpHeaders.AUTHORIZATION);
                attributes.put(SessionAttributeKey.Token.name(), token);
                //TODO: Map additional header parameter from client:
                //
                HttpSession session = servletRequest.getServletRequest().getSession();
                if (session != null) {
                    attributes.put(SessionAttributeKey.SessionID.name(), session.getId());
                    LOG.info("HttpSession ID: " + session.getId());
                }
                //
                JWTValidator validator = new JWTokenValidator(servletRequest.getServletRequest());
                boolean isValid = validator.isValid(token);
                return isValid;
                //
            }
        }
        return false;
    }

    protected MultiValueMap<String, String> getAuthParameters(ServletServerHttpRequest servletRequest) {

        LOG.info(servletRequest.getURI().toString());

        MultiValueMap<String, String> parameters =
                UriComponentsBuilder.fromUriString(servletRequest.getURI().toString()).build().getQueryParams();
        if (parameters.isEmpty()){
            parameters = servletRequest.getHeaders();
        }
        return parameters;
    }

    protected Map<String, String> getAuthHeaderParameters(ServletServerHttpRequest servletRequest){

        Map<String, String> map = new HashMap<>();

        MultiValueMap<String, String> headers = getAuthParameters(servletRequest);
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
