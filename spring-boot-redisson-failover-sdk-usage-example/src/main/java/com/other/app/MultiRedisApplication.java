package com.other.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
public class MultiRedisApplication {
    public static void main(String[] args) {
        SpringApplication.run(MultiRedisApplication.class, args);
    }



}
