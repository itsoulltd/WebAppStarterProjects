package com.infoworks.lab.webapp.config;

import com.infoworks.lab.cache.MemCache;
import com.infoworks.lab.datasources.RedisDataSource;
import com.infoworks.lab.datasources.RedissonDataSource;
import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.util.services.iResourceService;
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
        //RedissionClient instance are fully-thread safe.
        return Redisson.create(conf);
    }

    @Bean("userCache")
    MemCache<User> getUserCache(RedissonClient client){
        RedisDataSource dataSource = new RedissonDataSource(client);
        return new MemCache<>(dataSource, User.class);
    }

    @Bean
    public iResourceService getResourceService(){
        return iResourceService.create();
    }

}
