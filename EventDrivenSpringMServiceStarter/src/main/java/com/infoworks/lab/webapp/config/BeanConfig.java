package com.infoworks.lab.webapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infoworks.data.base.iMemorySource;
import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.services.RedissonDataSource;
import com.infoworks.objects.MessageParser;
import com.infoworks.utils.services.iResources;
import com.infoworks.data.cache.MemCache;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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

    @Bean
    ObjectMapper getObjectMapper(){
        //Solution: Add Jackson JSR-310 Module. Jackson doesn't know how to (de)serialize java.time.LocalDateTime,
        // because Java 8 time types are not supported out-of-the-box unless you register the JSR-310 module.
        ObjectMapper mapper = MessageParser.getJsonSerializer();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    RedissonClient getRedisClient(){
        String redisHost = env.getProperty("app.redis.host") != null
                ? env.getProperty("app.redis.host") : "localhost";
        String redisPort = env.getProperty("app.redis.port") != null
                ? env.getProperty("app.redis.port") : "6379";
        Config conf = new Config();
        conf.useSingleServer()
                .setAddress(String.format("redis://%s:%s",redisHost, redisPort))
                .setRetryAttempts(5)
                .setRetryInterval(1500);
        //Redisson-Client instance are fully-thread safe.
        return Redisson.create(conf);
    }

    @Bean("userCache")
    MemCache<User> getUserCache(RedissonClient client){
        iMemorySource dataSource = new RedissonDataSource(client);
        return new MemCache<>(dataSource, User.class);
    }

    @Bean
    public iResources getResourceService(){
        return iResources.create();
    }

}
