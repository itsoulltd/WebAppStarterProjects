package com.infoworks.lab.webapp.config.socket;

import com.infoworks.lab.webapp.config.socket.interceptor.ByPassAuthorizationInterceptor;
import com.infoworks.lab.webapp.config.socket.interceptor.WSocketAuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        /*
         * all destinations (request mappings in mvc / rest terms) will accept requests from destinations
         * prefixed with `/app`. For example, a @MessageMapping("/event") will receive messages from the
         * client when the client sends a message to "/app/event"
         */
        config.setApplicationDestinationPrefixes("/");

        /*
         * all subscription data (outgoing / responses) will send data to a URL the client can subscribe to
         * that begins with `/topic`. For example, a method with @SendTo("/outgoing") will allow a client
         * to subscribe on "/topic/outgoing" to receive messages from the websocket server
         * Additionally we need a '/queue' messaging channel (destination),
         * without '/queue' channel, We can't send asynchronous message from server to user session e.g. @destination("/queue/..")
         * This also implicitly create "/user/<session-id>/queue/...." messaging channel.
         * See spring-framework-web-socket documentation.
         */

        //Working With In-Memory Broker:
        //config.enableSimpleBroker("/queue", "/topic");

        //Enabling FullyFeatured MOM: e.g. ActiveMQ, RabbitMQ
        String relayHost = env.getProperty("app.activemq.host") != null
                ? env.getProperty("app.activemq.host") : "localhost";
        String relayPort = env.getProperty("app.activemq.stomp.port") != null
                ? env.getProperty("app.activemq.stomp.port") : "61613";
        //Configure fully-featured message-broker:
        config.enableStompBrokerRelay("/queue", "/topic")
                .setRelayHost(relayHost)
                .setRelayPort(Integer.valueOf(relayPort));
        //
    }

    @Value("${app.disable.security}")
    private boolean disableSecurity;

    @Override
    public void configureStompEndpoints(StompEndpointRegistry registry) {
        /*
        * Context of <app-name> comes from application.properties file's "server.servlet.context-path=/appName" property.
        * When we try to connect these end-point we have do as follow: ws://localhost:8080/<app-name>/process OR /listen
        * e.g. We send message to one of these endpoints like ws://localhost:8080/appName/process
        * If server.servlet.context-path is not set then we don't have to pass <app-name>
         */
        registry.addEndpoint("/listen","/process")
                .addInterceptors(disableSecurity
                        ? new ByPassAuthorizationInterceptor()
                        : new WSocketAuthorizationInterceptor())
                .setAllowedOrigins("*");

        //Enable SockJS fall back configuration:
        registry.addEndpoint("/listen","/process")
                .addInterceptors(disableSecurity
                        ? new ByPassAuthorizationInterceptor()
                        : new WSocketAuthorizationInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
