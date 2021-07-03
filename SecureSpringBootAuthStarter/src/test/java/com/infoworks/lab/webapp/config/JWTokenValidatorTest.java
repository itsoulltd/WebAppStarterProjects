package com.infoworks.lab.webapp.config;

import com.infoworks.lab.jjwt.JWTHeader;
import com.infoworks.lab.jjwt.JWTPayload;
import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.jwtoken.definition.TokenProvider;
import com.infoworks.lab.jwtoken.services.JWTokenProvider;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class JWTokenValidatorTest {

    @Test
    public void test(){
        JWTPayload payload = new JWTPayload().setSub("userName")
                .setIss("userName")
                .setIat(new Date().getTime())
                .setExp(TokenProvider.defaultTokenTimeToLive().getTimeInMillis())
                .addData("/new/account","false")
                .addData("/isValidToken","true");
        //
        TokenProvider token = new JWTokenProvider("SecretKeyToGenJWTs")
                .setPayload(payload)
                .setHeader(new JWTHeader().setTyp("round").setKid("112223344"));
        //
        String tokenKey = token.generateToken(TokenProvider.defaultTokenTimeToLive());
        //
        JWTValidator validator = new JWTokenValidator(null);
        boolean isTrue = validator.isValid(tokenKey, "SecretKeyToGenJWTs");
        Assert.assertTrue(isTrue);
    }

    @Test
    public void testStatic(){
        String tokenKey = "eyJraWQiOiJGUjRjT29IRURCIiwidHlwIjoicm91bmQiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE2MTQyMjY4MDYwNjUsIm5iZiI6MCwiZXhwIjoxNjE0MjMwNDA2MDY2LCJpc3MiOiJ0b3doaWQiLCJzdWIiOiJ0b3doaWQiLCJkYXRhIjp7Ii9pc1ZhbGlkVG9rZW4iOiJ0cnVlIiwiL25ldy9hY2NvdW50IjoiZmFsc2UifX0.xBhg59ndI1WB_xJ9llhyFDWJsq73ddBdyP_oHlD8rR3jyblaA35TR7IsYkIwb163M_tui_SEwX52JSIPgYtbnA";
        //
        JWTValidator validator = new JWTokenValidator(null);
        boolean isTrue = validator.isValid(tokenKey, "SecretKeyToGenJWTs");
        Assert.assertTrue(isTrue);
    }

}