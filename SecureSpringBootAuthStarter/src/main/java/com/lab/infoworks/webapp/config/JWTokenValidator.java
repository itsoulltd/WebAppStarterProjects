package com.lab.infoworks.webapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.jjwt.JWTHeader;
import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.jjwt.TokenValidator;
import com.infoworks.lab.jwtoken.definition.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode=ScopedProxyMode.TARGET_CLASS)
public class JWTokenValidator extends JWTValidator {

    private static Logger LOG = LoggerFactory.getLogger(JWTokenValidator.class.getSimpleName());

    @Autowired
    private HttpServletRequest request;

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

    protected String getHeaderValue(String key){
        if (request == null) return "";
        String value = request.getHeader(key);
        if (value == null){
            value = request.getParameter(key);
        }
        return value;
    }

    @Override
    public Boolean isValid(String token, String... args) {
        token = TokenValidator.parseToken(token, "Bearer ");
        //LOG.info(token);
        String[] parts = token.split("\\.");
        //LOG.info("HEADER: " + new String(Base64.getDecoder().decode(parts[0])));
        //LOG.info("PAYLOAD: " + new String(Base64.getDecoder().decode(parts[1])));
        try {
            ObjectMapper mapper = TokenProvider.getJsonSerializer();
            String headerPart = new String(Base64.getDecoder()
                    .decode(parts[0]));
            JWTHeader header = mapper.readValue(headerPart, JWTHeader.class);
            String secret = getSecret(header, args);
            byte[] bytes = this.validateSecret(secret);
            Key key = new SecretKeySpec(bytes, 0, bytes.length, header.getAlg());
            Jws<Claims> cl = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            Claims claims =  cl.getBody();
            LOG.info("JWT Claims: " + claims.toString());
            return true;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }
}
