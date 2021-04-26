package com.infoworks.lab.utils;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {

    @Test
    public void passwordTest(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPass = encoder.encode("12345");
        System.out.println( "GenPass: " + encodedPass);
        Assert.assertTrue(encoder.matches("12345", encodedPass));
        //
        Assert.assertFalse(encoder.matches("12345!@#", encodedPass));
    }

}
