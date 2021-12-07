package com.infoworks.lab.webapp.config;

import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.util.services.iResourceService;
import com.it.soul.lab.data.simple.SimpleDataSource;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.Duration;

@Configuration
public class BeanConfig {

    private Environment env;

    public BeanConfig(@Autowired Environment env) {
        this.env = env;
    }

    @Bean("HelloBean")
    public String getHello(){
        return "Hi Spring Hello";
    }

    @Bean("passengerDatasource")
    public SimpleDataSource<String, Passenger> getPassengerDatasource(){
        return new SimpleDataSource<>();
    }

    @Bean
    public RedisClient getRedisClient(){
        String redisHost = env.getProperty("app.redis.host") != null ? env.getProperty("app.redis.host") : "localhost";
        String redisPort = env.getProperty("app.redis.port") != null ? env.getProperty("app.redis.port") : "6379";
        RedisURI uri = RedisURI.builder()
                .withHost(redisHost)
                .withPort(Integer.valueOf(redisPort))
                .withTimeout(Duration.ofSeconds(30))
                .build();
        RedisClient client = RedisClient.create(uri);
        return client;
    }

    @Bean
    public StatefulRedisConnection<String, String> getRedisStatefulConnection(){
        return getRedisClient().connect();
    }

    @Bean
    public iResourceService getResourceService(){
        return iResourceService.create();
    }

}
