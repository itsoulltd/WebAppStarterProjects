package com.infoworks.domain.repositories;

import com.infoworks.config.TestBeanConfig;
import com.infoworks.config.TestJPAH2Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TestJPAH2Config.class, TestBeanConfig.class})
class SearchableRepositoryTest {

    @Test
    void search() {
        System.out.println("Test For " + SearchableRepositoryTest.class.getSimpleName());
    }
}