package com.microsoft.aa.redis.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "esm.backup.redis")
public class BackupRedisProperty extends RedisProperty {
}
