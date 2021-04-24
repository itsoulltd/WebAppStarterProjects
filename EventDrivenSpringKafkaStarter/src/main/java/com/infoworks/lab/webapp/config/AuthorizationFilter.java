package com.infoworks.lab.webapp.config;

import com.infoworks.lab.jjwt.JWTPayload;
import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.jjwt.TokenValidator;
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

public class AuthorizationFilter extends GenericFilterBean {

    public static final String AUTHORITIES_KEY = "roles";
    public static final String TOKEN_PREFIX = "Bearer ";
    private static Logger LOG = LoggerFactory.getLogger("AuthorizationFilter");

    public AuthorizationFilter() {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        LOG.info("RequestURI: " + request.getRequestURI().toLowerCase());
        if(header == null || !header.startsWith(TOKEN_PREFIX.trim())) {
            chain.doFilter(request,response);
            return;
        }
        Authentication authenticationToken = getAuthentication(request);
        if (authenticationToken != null){
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request,response);
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    public Authentication getAuthentication(HttpServletRequest request){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(token != null){
            token = token.replace(TOKEN_PREFIX.trim(),"").trim();
            try {
                JWTValidator validator = new JWTokenValidator(request);
                boolean isTrue = validator.isValid(token);
                if(isTrue) {
                    JWTPayload payload = TokenValidator.parsePayload(token, JWTPayload.class);
                    Object authoritiesClaim = (payload.getData() == null)
                            ? null
                            : payload.getData().get(AUTHORITIES_KEY);
                    Collection<? extends GrantedAuthority> authorities = (authoritiesClaim == null)
                            ? AuthorityUtils.NO_AUTHORITIES
                            : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());
                    //Here We Go!
                    User principal = new User(payload.getSub(), "", authorities);
                    return new UsernamePasswordAuthenticationToken(principal, token, authorities); //Passing authorities here is important for @EnableGlobalMethodSecurity(...)
                }
            } catch (RuntimeException e) {
                LOG.error(e.getMessage(), e);
            }
            return null;
        }
        return null;
    }
}
