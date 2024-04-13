package com.infoworks.lab.webapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class ByPassAuthorizationFilter extends GenericFilterBean {

    private static Logger LOG = LoggerFactory.getLogger("ByPassAuthorizationFilter");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        token = (token != null)
                ? token.replace(AuthorizationFilter.TOKEN_PREFIX.trim(),"").trim()
                : "";
        LOG.info("Auth-Token:" + token);
        LOG.info("RequestURI: " + request.getRequestURI().toLowerCase());
        //
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN,ROLE_ADMIN"); //AuthorityUtils.NO_AUTHORITIES;
        User principal = new User("TEST_ADMIN", "root@!@#", authorities);
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(principal, token, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
