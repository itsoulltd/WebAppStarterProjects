package com.infoworks.lab.services;

import com.infoworks.lab.domain.entities.Gender;
import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.webapp.WebApplicationTest;
import com.infoworks.lab.webapp.config.BeanConfig;
import com.infoworks.lab.webapp.config.TestJPAH2Config;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebApplicationTest.class, BeanConfig.class, TestJPAH2Config.class, UserService.class})
@Transactional
@TestPropertySource(locations = {"classpath:application-h2db.properties"})
public class UserServiceUnitTest {

    @Before
    public void setup(){
        /**/
    }

    @Autowired
    private UserService service;

    @Test
    public void initTest() {
        Assert.assertNotNull(service);
        System.out.println("UserService injected: Yes");
    }

    @Test
    public void insert() {
        User user = new User("Sayed The Awesome Coder", "sayde@g.com", Gender.MALE, 24);
        //Call service to make the save:
        service.put(null, user);
        //Verify:
        User newUser = service.read("Sayed The Awesome Coder");
        assertNotNull(newUser);
        assertNotNull(newUser.getId());
        System.out.println(newUser.marshallingToMap(true));
    }

    @Test
    public void update() {}

    @Test
    public void delete() {}

    @Test
    public void count() {
        User user = new User("Sayed The Awesome Coder", "sayde@g.com", Gender.MALE, 24);
        //Call service to make the save:
        service.put(null, user);
        //Verify:
        long count = service.size();
        Assert.assertTrue(count == 1);
    }

    @Test
    public void fetch() {
        service.put(null, new User("Sayed The Awesome Coder", "sayde@g.com", Gender.MALE, 24));
        service.put(null, new User("Evan The Pankha Coder", "evan@g.com", Gender.MALE, 24));
        service.put(null, new User("Razib The Pagla", "razib@g.com", Gender.MALE, 26));
        //
        List<User> paged = Arrays.asList(service.readSync(0, 10));
        Assert.assertTrue(!paged.isEmpty());
        paged.forEach(user -> System.out.println(user.getName()));
    }
}
