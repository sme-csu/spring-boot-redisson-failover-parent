package com.other.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
@ComponentScan(basePackages = {"com.esm.core.cache.lock"})
@RestController
public class MultiRedisApplication {


    @Autowired
    com.esm.core.cache.lock.DistributedRedisLock distributedRedisLock;

    public static void main(String[] args) {
        SpringApplication.run(MultiRedisApplication.class, args);
    }

    @GetMapping("/lockRefresh")
    public String refreshLock(){
      Boolean tryResult=  distributedRedisLock.tryLock("test");
        return "result is " + tryResult;
    }


}
