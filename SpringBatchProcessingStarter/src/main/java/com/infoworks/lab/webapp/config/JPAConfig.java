package com.infoworks.lab.webapp.config;

import com.infoworks.lab.jsql.JsqlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.infoworks.lab.domain.repositories"},
        transactionManagerRef = "jpa-transaction-ref"
)
@PropertySource("classpath:mysql-db.properties")
public class JPAConfig {

    private Environment env;
    public JPAConfig(@Autowired Environment env) {
        this.env = env;
    }

    @Bean
    JsqlConfig getJsqlConfig(DataSource dataSource){
        return new JsqlConfig(dataSource);
    }

    @Bean("AppDBNameKey")
    String appDBNameKey(){
        return env.getProperty("app.db.name");
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

    @Primary
    @Bean
    public DataSource dataSource(){
        DataSource dataSource = DataSourceBuilder
                .create()
                .username(username)
                .password(password)
                .url(url)
                .driverClassName(driverClassName)
                .build();
        return dataSource;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder
            , DataSource dataSource){
        return builder
                .dataSource(dataSource)
                .packages("com.infoworks.lab.domain.entities")
                .persistenceUnit(persistenceUnitName)
                .build();
    }

    @Bean("jpa-transaction-ref")
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory){
        return new JpaTransactionManager(entityManagerFactory);
    }

}
