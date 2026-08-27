package com.infoworks.config;

import com.infoworks.domain.entities.Username;
import com.infoworks.sql.executor.SQLExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.util.Optional;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.infoworks.domain.repositories"}
)
@PropertySource("classpath:application-h2db.properties")
public class JPAConfig {

    private static Logger LOG = LoggerFactory.getLogger(JPAConfig.class);
    private Environment env;

    public JPAConfig(Environment env) {
        this.env = env;
    }

    @Value("${spring.datasource.driver-class-name}")
    String driverClassName;
    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String password;
    @Value("${app.db.name}")
    String persistenceUnitName;

    @Primary @Bean
    public DataSource dataSource() {
        DataSource dataSource = DataSourceBuilder
                .create()
                .username(username)
                .password(password)
                .url(url)
                .driverClassName(driverClassName)
                .build();
        return dataSource;
    }

    @Primary @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, DataSource dataSource){
        return builder
                .dataSource(dataSource)
                .packages("com.infoworks.domain.entities")
                .persistenceUnit(persistenceUnitName)
                .build();
    }

    @Bean @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    //Since SQLExecutor is a WebApplicationContext.SCOPE_REQUEST Variable, it will automatically close connection when garbage-collected.
    public SQLExecutor executor(DataSource dataSource) throws Exception {
        SQLExecutor exe = new SQLExecutor(dataSource.getConnection());
        LOG.info("Executor-Connection Has been Created.");
        return exe;
    }

    @Bean
    public AuditorAware<Username> auditor() {
        return () -> Optional.of(new Username("TEST-ADMIN"));
    }

}
