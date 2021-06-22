package com.other.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisFailoverAppTest {

    @Test
    void contextLoads() {
        MultiRedisApplication.main(new String[] {});
    }
}
