package com.infoworks.lab.webapp.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = {"com.infoworks.lab.domain.repositories"}
)
@PropertySource("classpath:mongo-db.properties")
public class MongoConfig {

    @Value("${mongo.db.url}") String url;
    @Value("${mongo.db.username}") String username;
    @Value("${mongo.db.password}") String password;
    @Value("${mongo.db.name}") String persistenceUnitName;

    @Bean
    public MongoClient mongo() {
        ConnectionString connectionString = new ConnectionString(url);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), persistenceUnitName);
    }

}
