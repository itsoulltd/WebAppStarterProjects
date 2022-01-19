package com.infoworks.lab.webapp.config.socket;

import com.infoworks.lab.webapp.config.socket.interceptor.ByPassAuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class SocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<Session> {

    private Environment env;

    public SocketConfig(@Autowired Environment env) {
        this.env = env;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //This prefix appended before the message destination, e.g. @MessageMapping("/event")
        config.setApplicationDestinationPrefixes("/");
        //With out having a "/queue" as a destination,
        //We can't send asynchronous message from server to user session e.g. @destination("/queue/..")
        //This also implicitly create "/user/<session-id>/queue/...." messaging channel.
        //We subscribe to the consume or queue topic:

        //Working With In-Memory Broker:
        //config.enableSimpleBroker("/queue", "/topic");

        //Enabling FullyFeatured MOM: ActiveMQ
        String relayHost = env.getProperty("app.activemq.host") != null
                ? env.getProperty("app.activemq.host") : "localhost";
        String relayPort = env.getProperty("app.activemq.stomp.port") != null
                ? env.getProperty("app.activemq.stomp.port") : "61613";
        //
        config.enableStompBrokerRelay("/queue", "/topic")
                .setRelayHost(relayHost)
                .setRelayPort(Integer.valueOf(relayPort));
        //
    }

    @Override
    public void configureStompEndpoints(StompEndpointRegistry registry) {

        //Context of <app-name> comes from application.properties file's "server.servlet.context-path=/appName" property.
        //When we try to connect these end-point we have do as follow: ws://localhost:8080/<app-name>/process OR /listen
        //e.g. We send message to one of these endpoints like ws://localhost:8080/appName/process
        //If server.servlet.context-path is not set then we don't have to pass <app-name>
        registry.addEndpoint("/listen","/process")
                .addInterceptors(new ByPassAuthorizationInterceptor()) //new WSocketAuthorizationInterceptor()
                .setAllowedOrigins("*");

        //Enable SockJS fall back configuration.
        registry.addEndpoint("/listen","/process")
                .addInterceptors(new ByPassAuthorizationInterceptor()) //new WSocketAuthorizationInterceptor()
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
