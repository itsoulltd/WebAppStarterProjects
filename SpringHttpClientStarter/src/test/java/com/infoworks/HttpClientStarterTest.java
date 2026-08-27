package com.infoworks;

import com.infoworks.config.BeanConfig;
import com.infoworks.config.TestJPAH2Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest(classes = {HttpClientStarterTest.class, BeanConfig.class, TestJPAH2Config.class})
@ComponentScan(basePackages = {"com.infoworks.domain", "com.infoworks.services"})
public class HttpClientStarterTest {

    @Test
    void contextLoads() {
        System.out.println("Loaded");
    }

}
