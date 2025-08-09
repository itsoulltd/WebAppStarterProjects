package com.infoworks.lab.domain.entities;

import com.infoworks.lab.webapp.config.ValidationConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class UserTest {

    @Test
    public void validationFailedTest() {
        User user = new User();
        Optional<String> message = ValidationConfig.validateWithMessage(user);
        Assert.assertTrue(message.isPresent());
        System.out.println("Error: \n" + message.orElse("Not found!"));
    }

    @Test
    public void validationSuccessTest() {
        User user = new User();
        user.setEmail("towhid.islam@gmail.com");
        user.setName("Towhid Islam");
        Optional<String> message = ValidationConfig.validateWithMessage(user);
        Assert.assertTrue(message.isPresent() == false);
        System.out.println("Error: \n" + message.orElse("Not found!"));
    }

}