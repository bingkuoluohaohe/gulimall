package com.jnu.gulimall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * [一句话描述该类的功能]
 *
 * @author : [游成鹤]
 * @version : [v1.0]
 * @date : [2022/9/18 17:32]
 */
@EnableRedisHttpSession
@EnableCaching
@MapperScan("com.jnu.gulimall.dao")
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = "com.jnu.gulimall")
public class GulimallApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallApplication.class, args);
    }
}