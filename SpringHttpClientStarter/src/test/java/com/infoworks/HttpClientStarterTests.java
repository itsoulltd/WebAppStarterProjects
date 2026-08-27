package com.infoworks;

import com.infoworks.config.BeanConfig;
import com.infoworks.config.TestJPAH2Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest(classes = {HttpClientStarterTests.class, BeanConfig.class, TestJPAH2Config.class})
@ComponentScan(basePackages = {"com.infoworks.domain", "com.infoworks.services"})
public class HttpClientStarterTests {

    @Test
    void contextLoads() {
        System.out.println("Loaded");
    }

}
