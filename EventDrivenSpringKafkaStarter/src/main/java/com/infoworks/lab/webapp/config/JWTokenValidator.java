package com.infoworks.lab.webapp.config;

import com.infoworks.lab.jjwt.JWTHeader;
import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.jjwt.TokenValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode=ScopedProxyMode.TARGET_CLASS)
public class JWTokenValidator extends JWTValidator {

    @Autowired
    private HttpServletRequest request;

    protected String getHeaderValue(String key){
        if (request == null) return "";
        String value = request.getHeader(key);
        if (value == null){
            value = request.getParameter(key);
        }
        return value;
    }

    @Override
    protected String getSecret(JWTHeader header, String... args) throws Exception {
        //TODO:
        return "";
    }

    @Override
    public String getUserID(String token, String... args) {
        if (token == null || token.isEmpty()){
            token = TokenValidation.parseToken(getHeaderValue(HttpHeaders.AUTHORIZATION), "Bearer ");
        }
        return super.getUserID(token, args);
    }

    @Override
    public String getIssuer(String token, String... args) {
        if (token == null || token.isEmpty()){
            token = TokenValidation.parseToken(getHeaderValue(HttpHeaders.AUTHORIZATION), "Bearer ");
        }
        return super.getIssuer(token, args);
    }

    @Override
    public String getTenantID(String token, String... args) {
        if (token == null || token.isEmpty()){
            token = TokenValidation.parseToken(getHeaderValue(HttpHeaders.AUTHORIZATION), "Bearer ");
        }
        return super.getTenantID(token, args);
    }

}
