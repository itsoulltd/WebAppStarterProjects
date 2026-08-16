package com.infoworks.lab.webapp.config;

import com.infoworks.connect.JDBCDriverClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupConfig implements CommandLineRunner {

    private final String serverDomain;
    private final String serverPort;
    private final String servletContext;
    private final String activeDriverClass;
    private final String isH2ConsoleEnabled;
    private final String h2ConsolePath;

    public StartupConfig(@Value("${server.app.domain}") String serverDomain
            , @Value("${server.port}") String serverPort
            , @Value("${server.servlet.context-path}") String servletContext
            , @Value("${spring.datasource.driver-class-name}") String activeDriverClass
            , @Value("${spring.h2.console.enabled}") String isH2ConsoleEnabled
            , @Value("${spring.h2.console.path}") String h2ConsolePath) {
        this.serverDomain = serverDomain;
        this.serverPort = serverPort;
        this.servletContext = servletContext;
        this.activeDriverClass = activeDriverClass;
        this.isH2ConsoleEnabled = isH2ConsoleEnabled;
        this.h2ConsolePath = h2ConsolePath;
    }

    @Override
    public void run(String... args) throws Exception {
        //How to use executor:
        System.out.println(String.format("http://%s:%s%s/swagger-ui/index.html", serverDomain, serverPort, servletContext));
        if (activeDriverClass.equalsIgnoreCase(JDBCDriverClass.H2_EMBEDDED.toString())
                && Boolean.parseBoolean(isH2ConsoleEnabled)){
            System.out.println(String.format("http://%s:%s%s%s", serverDomain, serverPort, servletContext, h2ConsolePath));
        }
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
