package com.infoworks.lab.webapp.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Redirect HTTP requests to HTTPS
 *
 * Now that we have enabled HTTPS in our Spring Boot application and blocked any HTTP request, we want to redirect all traffic to HTTPS.
 * Spring allows defining just one network connector in application.properties (or application.yml).
 * Since we have used it for HTTPS, we have to set the HTTP connector programmatically for our Tomcat web server.
 */

@Configuration
public class ServerConfig {

    @Autowired
    private Environment env;

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(getHttpConnector());
        return tomcat;
    }

    private Connector getHttpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(Integer.valueOf(env.getProperty("server.port.http")));
        connector.setSecure(false);
        connector.setRedirectPort(Integer.valueOf(env.getProperty("server.port")));
        return connector;
    }

}
