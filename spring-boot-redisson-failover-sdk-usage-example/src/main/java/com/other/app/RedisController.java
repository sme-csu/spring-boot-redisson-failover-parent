package com.other.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;

@RestController
public class RedisController {
    private final static Logger logger = LoggerFactory.getLogger(MultiRedisApplication.class);

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate masterRedisTemplate;

    @GetMapping("/redis")
    public String run() throws Exception {

        masterRedisTemplate.opsForValue().set("hello", "world_1");

        String primaryKeyValue = masterRedisTemplate.opsForValue().get("hello").toString();
        String secondaryKeyValue = masterRedisTemplate.opsForValue().get("hello").toString();

        logger.info("==================================================================");
        logger.info(String.format("read the primary redis, key is `hello`, value is %s", primaryKeyValue));
        logger.info(String.format("read the secondary redis, key is `hello`, value is %s", secondaryKeyValue));
        logger.info("==================================================================");

        //获取本机IP地址
        System.out.println(InetAddress.getLocalHost().getHostAddress());
        //获取地址
        System.out.println(InetAddress.getByName("aa-cluster-master.redis.cache.chinacloudapi.cn").getHostAddress());
        //获取真实IP地址
        System.out.println(InetAddress.getByName("aa-cluster-master.redis.cache.chinacloudapi.cn"));
        //获取配置在HOST中的域名IP地址
        System.out.println(InetAddress.getByName("aa-cluster-master.redis.cache.chinacloudapi.cn").getHostAddress());

        return "read the primary redis, key is `hello`, value is " + primaryKeyValue;
    }
}
