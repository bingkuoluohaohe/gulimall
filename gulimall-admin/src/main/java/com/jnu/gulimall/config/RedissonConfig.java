package com.jnu.gulimall.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author ych
 * @date 2022/9/15 21:14
 */
@Configuration
public class RedissonConfig {
    /**
     * 所有对Redisson的操作都是通过redissonClient对象
     */
    @Bean(name = "redisson")
    public RedissonClient redisson(@Value("${spring.redis.host}") String host, @Value("${spring.redis.password}") String password) throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":6379");
        config.useSingleServer().setPassword(password);
        return Redisson.create(config);
    }
}
