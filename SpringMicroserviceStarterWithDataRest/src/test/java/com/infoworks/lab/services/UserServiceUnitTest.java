package com.infoworks.lab.services;

import com.infoworks.lab.domain.repositories.UserRepository;
import com.infoworks.lab.webapp.config.TestJPAH2Config;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {TestJPAH2Config.class})
public class UserServiceUnitTest {

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    UserRepository repository;

    @Test
    public void happyPathTest(){
        //Defining Mock Object:
        //User aUser = new User("Towhid", Gender.MALE, 36);
        //when(repository.save(any(User.class))).thenReturn(aUser);
        //
    }
}
