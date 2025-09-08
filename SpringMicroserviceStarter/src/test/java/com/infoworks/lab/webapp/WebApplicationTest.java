package com.infoworks.lab.webapp;

import com.infoworks.lab.webapp.config.BeanConfig;
import com.infoworks.lab.webapp.config.TestJPAH2Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK
        , classes = {WebApplicationTest.class, BeanConfig.class, TestJPAH2Config.class})
@AutoConfigureMockMvc
@ComponentScan(basePackages = {"com.infoworks.lab.controllers", "com.infoworks.lab.services", "com.infoworks.lab.domain"})
public class WebApplicationTest {
    @Test
    public void contextLoads() {
        System.out.println("ApplicationContext Loaded Successfully");
    }
}