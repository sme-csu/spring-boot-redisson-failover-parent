package com.microsoft.aa.redis.config;

import com.microsoft.aa.redis.property.BackupRedisProperty;
import com.microsoft.aa.redis.property.MasterRedisProperty;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.connection.balancer.RoundRobinLoadBalancer;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@Configuration
@ComponentScan(basePackages = {"com.microsoft.aa.redis.config"})
@EnableConfigurationProperties({BackupRedisProperty.class, MasterRedisProperty.class})
@EnableScheduling
public class RedisFailoverAutoConfiguration {

    @Autowired
    MasterRedisProperty masterRedisProperty;


    @Bean("redisConfig")
    public Config redisConfig()throws  IOException{
        Config config = new Config();
        config.useClusterServers()
                .setPassword(masterRedisProperty.getPassword())
                .setReadMode(ReadMode.MASTER)
                .setIdleConnectionTimeout(masterRedisProperty.getIdleConnectionTimeout())
                .setConnectTimeout(masterRedisProperty.getConnectTimeout())
                .setTimeout(masterRedisProperty.getTimeout())
                .setScanInterval(masterRedisProperty.getScanInterval())
                .setRetryAttempts(masterRedisProperty.getAttempts())
                .setRetryInterval(masterRedisProperty.getInterval())
                .setLoadBalancer(new RoundRobinLoadBalancer())
                // 主节点变化扫描间隔时间
                .setScanInterval(masterRedisProperty.getScanInterval())
                .addNodeAddress(masterRedisProperty.getNodes());
        return config;
    }

    @Bean("redissonClient")
    public RedissonClient redissonClient(Config config) throws IOException {
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

    @Bean("redissonConnectionFactory")
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

    @Bean("redisTemplate")
    public RedisTemplate<String, String> redisTemplate(RedissonConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        setSerializer(template);//设置序列化工具
        template.afterPropertiesSet();
        return template;
    }

    private void setSerializer(StringRedisTemplate template) {
        StringRedisSerializer defaultSerializer = new StringRedisSerializer();
        template.setKeySerializer(defaultSerializer);
        template.setValueSerializer(defaultSerializer);
    }

}
