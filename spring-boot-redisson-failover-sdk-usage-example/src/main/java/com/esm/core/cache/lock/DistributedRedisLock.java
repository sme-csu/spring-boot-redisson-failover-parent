package com.esm.core.cache.lock;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Title: DistributedRedisLock</p >
 * <p>Description: 分布式锁实现类</p >
 * @author wanggq
 * @date 2020-8-4
 */
@Component
public class DistributedRedisLock {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 默认超时时间30s
     *
     * @param key
     */
    public void lock(String key) {
        RLock lock = redissonClient.getLock(key);
        lock.lock();
    }

    /**
     * 未获取到锁直接返回false
     *
     * @param key
     * @return
     */
    public boolean tryLock(String key) {
        RLock lock = redissonClient.getLock(key);
        return lock.tryLock();
    }

    public void lock(String lockKey, TimeUnit unit, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, unit);
    }

    /**
     * 尝试获取锁(会转变为同步锁)
     *
     * @param lockKey
     * @param unit      时间单位
     * @param waitTime  最多等待时间
     * @param leaseTime 上锁后自动释放锁时间
     * @throws RuntimeException
     * @return
     */
    public boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) throws Exception {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (Exception e) {
            throw new RuntimeException("redis加锁异常: ", e);
        }
    }

    /**
     * 释放锁
     *
     * @param lockKey
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}