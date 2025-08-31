package com.infoworks.lab.webapp.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RouteExecutionLogger extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(RouteExecutionLogger.class);

    @Override
    public void doFilter(ServletRequest servletRequest
            , ServletResponse servletResponse
            , FilterChain filterChain) throws IOException, ServletException {
        //Elapsed time between request and response:
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        filterChain.doFilter(servletRequest, servletResponse);
        stopWatch.stop();
        //Get the info from request & response:
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        LOG.info("{}:{} took {}ms and returned {}"
                , req.getMethod(), req.getRequestURI(), stopWatch.getTotalTimeMillis(), res.getStatus());
    }
}
