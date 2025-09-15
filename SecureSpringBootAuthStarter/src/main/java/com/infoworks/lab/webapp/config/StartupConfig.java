package com.infoworks.lab.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupConfig implements CommandLineRunner {

    @Value("${server.app.domain}")
    private String serverDomain;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.servlet.context-path}")
    private String servletContext;

    @Override
    public void run(String... args) throws Exception {
        //How to use executor:
        System.out.println(String.format("https://%s:%s%s/swagger-ui/index.html", serverDomain, serverPort, servletContext));
        //
    }

    @EventListener
    public void handleContextStartedListener(ContextRefreshedEvent event){
        System.out.println("ContextStarted....");
    }

    @EventListener
    public void handleContextStoppedListener(ContextClosedEvent event){
        System.out.println("ContextStopped....");
    }
}
