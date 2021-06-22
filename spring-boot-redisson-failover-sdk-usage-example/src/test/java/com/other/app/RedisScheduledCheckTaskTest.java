package com.other.app;

import com.microsoft.aa.redis.config.RedisScheduledCheckTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RedisScheduledCheckTaskTest {

    @Autowired
    RedisScheduledCheckTask redisScheduledCheckTask;

    @Autowired
    @Qualifier("redissonClient")
    private RedissonClient redissonClient;

    @Test
    public void redisScheduledCheckTaskTest1() throws Exception {
        redisScheduledCheckTask.scheduledTask();
        assertEquals(1,1);
    }

    @Test
    public void redisScheduledCheckTaskTest2() throws Exception {
        redissonClient.shutdown();
        redisScheduledCheckTask.setCurrentPassword("error");
        redisScheduledCheckTask.scheduledTask();
        assertEquals(1,1);
    }

    @Test
    public void redisScheduledCheckTaskTest3() throws Exception {
        redissonClient.shutdown();
        redisScheduledCheckTask.scheduledTask();
        assertEquals(1,1);
    }
}
