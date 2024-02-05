package com.infoworks.lab.webapp.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableMongoRepositories(
        basePackages = {"com.infoworks.lab.domain.repositories"}
)
@PropertySource("classpath:application-mongo.properties")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}") String url;
    @Value("${mongo.db.username}") String username;
    @Value("${mongo.db.password}") String password;
    @Value("${mongo.db.name}") String persistenceUnitName;

    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(url);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient client) throws Exception {
        return new MongoTemplate(client, persistenceUnitName);
    }

    @Bean
    public GridFsTemplate gridFsTemplate(MappingMongoConverter converter) {
        return new GridFsTemplate(mongoDbFactory(), converter);
    }

    @Override
    protected String getDatabaseName() {
        return persistenceUnitName;
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        return Stream.of("com.infoworks.lab.domain.entities")
                .collect(Collectors.toList());
    }

}
