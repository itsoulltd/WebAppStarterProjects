package com.infoworks.lab.webapp.config.socket.session;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

import javax.servlet.ServletContext;

@Configuration
@EnableRedisHttpSession
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {

    @Override
    protected void beforeSessionRepositoryFilter(ServletContext servletContext) {
        super.beforeSessionRepositoryFilter(servletContext);
    }

    @Override
    protected void afterSessionRepositoryFilter(ServletContext servletContext) {
        super.afterSessionRepositoryFilter(servletContext);
    }
}
