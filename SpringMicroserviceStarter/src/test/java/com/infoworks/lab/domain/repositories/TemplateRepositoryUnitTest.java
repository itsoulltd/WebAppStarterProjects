package com.infoworks.lab.domain.repositories;

import com.infoworks.lab.webapp.config.TestJPAConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestJPAConfig.class})
@Transactional
@TestPropertySource(locations = {"classpath:h2-db.properties"})
public class TemplateRepositoryUnitTest {

    /*@Autowired
    TemplateRepository repository;*/

    @Test
    public void insert(){}

    @Test
    public void update(){}

    @Test
    public void delete(){}

    @Test
    public void count(){}

    @Test
    public void fetch(){}

}
