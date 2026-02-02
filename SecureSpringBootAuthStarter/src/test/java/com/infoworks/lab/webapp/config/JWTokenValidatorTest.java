package com.infoworks.lab.webapp.config;

import com.infoworks.utils.jwt.TokenProvider;
import com.infoworks.utils.jwt.impl.JWebToken;
import com.infoworks.utils.jwt.models.JWTHeader;
import com.infoworks.utils.jwt.models.JWTPayload;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JWTokenValidatorTest {

    @Test
    public void test(){
        JWTPayload payload = new JWTPayload().setSub("userName")
                .setIss("userName")
                .setIat(new Date().getTime())
                .setExp(TokenProvider.timeToLive(Duration.ofHours(1), TimeUnit.HOURS).getTimeInMillis())
                .addData("/new/account","false")
                .addData("/isValidToken","true");
        //
        TokenProvider token = new JWebToken();
        String tokenKey = token.generateToken("SecretKeyToGenJWTs", new JWTHeader().setTyp("round").setKid("112223344"), payload);
        //
        TokenProvider validator = new JWTokenValidator(null);
        boolean isTrue = validator.isValid(tokenKey, "SecretKeyToGenJWTs");
        Assert.assertTrue(isTrue);
    }

    @Test
    public void testStatic(){
        String tokenKey = "eyJraWQiOiJGUjRjT29IRURCIiwidHlwIjoicm91bmQiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE2MTQyMjY4MDYwNjUsIm5iZiI6MCwiZXhwIjoxNjE0MjMwNDA2MDY2LCJpc3MiOiJ0b3doaWQiLCJzdWIiOiJ0b3doaWQiLCJkYXRhIjp7Ii9pc1ZhbGlkVG9rZW4iOiJ0cnVlIiwiL25ldy9hY2NvdW50IjoiZmFsc2UifX0.xBhg59ndI1WB_xJ9llhyFDWJsq73ddBdyP_oHlD8rR3jyblaA35TR7IsYkIwb163M_tui_SEwX52JSIPgYtbnA";
        //
        TokenProvider validator = new JWTokenValidator(null);
        boolean isTrue = validator.isValid(tokenKey, "SecretKeyToGenJWTs");
        Assert.assertTrue(isTrue);
    }

}