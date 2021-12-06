package com.infoworks.lab.webapp.config;

import com.infoworks.lab.jjwt.JWTHeader;
import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.jjwt.TokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode=ScopedProxyMode.TARGET_CLASS)
public class JWTokenValidator extends JWTValidator {

    private HttpServletRequest request;

    public JWTokenValidator(@Autowired HttpServletRequest request) {
        this.request = request;
    }

    protected String getHeaderValue(String key){
        if (request == null) return "";
        String value = request.getHeader(key);
        if (value == null){
            value = request.getParameter(key);
        }
        return value;
    }

    public static String getRandomSecretKey(){
        int randIndex = new Random().nextInt(JWTokenValidator.getSecretKeyMap().size());
        String kid = JWTokenValidator.getSecretKeyMap().keySet().toArray(new String[0])[randIndex];
        return kid;
    }

    public static Map<String, String> getSecretKeyMap(){
        Map<String, String> keyMap = new ConcurrentHashMap<>();
        keyMap.put("f5bbeuWIqr","Ye0OPI4FUYTzKwe");
        keyMap.put("gtrecmgy7U","gQpfEJFFRDEP1Nc");
        keyMap.put("e786n188ml","EMWOPLMdhZ0nKvz");
        return keyMap;
    }

    @Override
    protected String getSecret(JWTHeader header, String... args) throws Exception {
        String secret = getSecretKeyMap().get(header.getKid());
        return secret != null ? secret : super.getSecret(header, args);
    }

    @Override
    public String getUserID(String token, String... args) {
        if (token == null || token.isEmpty()){
            token = TokenValidator.parseToken(getHeaderValue(HttpHeaders.AUTHORIZATION), "Bearer ");
        }
        return super.getUserID(token, args);
    }

    @Override
    public String getIssuer(String token, String... args) {
        if (token == null || token.isEmpty()){
            token = TokenValidator.parseToken(getHeaderValue(HttpHeaders.AUTHORIZATION), "Bearer ");
        }
        return super.getIssuer(token, args);
    }

}
