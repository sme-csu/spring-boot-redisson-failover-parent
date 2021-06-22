package com.microsoft.aa.redis.config;


import com.microsoft.aa.redis.property.BackupRedisProperty;
import com.microsoft.aa.redis.property.MasterRedisProperty;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.connection.balancer.RoundRobinLoadBalancer;
import org.redisson.spring.data.connection.RedissonConnection;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.ClusterInfo;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Properties;

@Component
@ConditionalOnProperty("esm.backup.redis.password")
public class RedisScheduledCheckTask implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(RedisScheduledCheckTask.class);


    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    private String currentPassword;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    public Config getRedisConfig() {
        return redisConfig;
    }

    public void setRedisConfig(Config redisConfig) {
        this.redisConfig = redisConfig;
    }

    @Autowired
    @Qualifier("redisConfig")
    private Config redisConfig;

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Autowired
    @Qualifier("redissonClient")
    private RedissonClient redissonClient;

    @Autowired
    @Qualifier("redissonConnectionFactory")
    private RedissonConnectionFactory redissonConnectionFactory;

    @Autowired
    private MasterRedisProperty masterRedisProperty;

    @Autowired
    private BackupRedisProperty backupRedisProperty;

    @Scheduled(fixedRate = 3000)
    public void scheduledTask() throws Exception {
        logger.debug("任务执行时间：" + new Date());


        RedissonConnectionFactory currentRedisConnectionFactory = (RedissonConnectionFactory) redisTemplate.getConnectionFactory();
        RedisConnection connection = RedisConnectionUtils.getConnection(currentRedisConnectionFactory);

        try {
            if (connection instanceof RedissonConnection) {
                ClusterInfo clusterInfo = currentRedisConnectionFactory.getClusterConnection().clusterGetClusterInfo();
                logger.debug("****cluster_size " + clusterInfo.getClusterSize());
            } else {
                Properties info = connection.info();
                logger.debug("****version " + info.getProperty("redis_version"));
            }
            // if the status is ok, then break,wait next time.
            return;
        }catch (Exception e)
        {
            // if the status is NG, then try again.
            logger.error("redis connection exception:" + e.getMessage());
        }
        finally {
            RedisConnectionUtils.releaseConnection(connection, currentRedisConnectionFactory);
        }

        logger.debug("The application cannot connect the redis server...");

        // the final status is NG, switch connection string
        logger.info("****switch connection...");
        try{
            if(!redissonClient.isShutdown()){
                redissonClient.shutdown();
                currentRedisConnectionFactory.destroy();
            }
        Config changedConfig = new Config();
        if(getCurrentPassword().equals(masterRedisProperty.getPassword())){
            logger.info("****switch backup connection...");
            changedConfig.useClusterServers()
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
                    .addNodeAddress(masterRedisProperty.getNodes())
                    // password is different
                    .setPassword(backupRedisProperty.getPassword());
            setCurrentPassword(backupRedisProperty.getPassword());
        }else{
            logger.info("****switch master connection...");
            changedConfig.useClusterServers()
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
                    .addNodeAddress(masterRedisProperty.getNodes())
                    // password is different
                    .setPassword(masterRedisProperty.getPassword());
            setCurrentPassword(masterRedisProperty.getPassword());
        }
        RedissonClient changedRedissonClient = Redisson.create(changedConfig);
        setRedisConfig(changedConfig);
        RedissonConnectionFactory connectionFactory = new RedissonConnectionFactory(changedRedissonClient);
        connectionFactory.afterPropertiesSet();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
        }catch (Exception e){
            logger.error("****switch  connection failed. try again next time...");
        }
    }

    public void afterPropertiesSet() throws Exception {
        setCurrentPassword(redisConfig.useClusterServers().getPassword());
    }
}
