package com.tengjiao.part.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonClientTool {

    private RedissonClient redissonClient;

    public RedissonClientTool(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 尝试获得锁（悲观锁）
     *
     * @param lockKey
     * @param waitTime  等待时间,单位毫秒。等待时间waitTime内获取不到锁，则直接返回；租约时间leaseTime后强制释放锁
     * @param leaseTime 租约时间,单位毫秒。0或负数 自动续租
     * @description
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        boolean suc;
        try {
            RLock lock = redissonClient.getLock(lockKey);
            if (leaseTime > 0) {
                suc = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            } else {
                suc = lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
            }
        } catch (Throwable e) {
            String msg = String.format("LOCK FAILED: key=%s||tryLockTime=%s||lockExpiredTime=%s", lockKey, waitTime, leaseTime);
            throw new IllegalStateException(msg, e);
        }
        return suc;
    }

    /**
     * 释放锁
     */
    public void unlock(String lockKey) {
        try {
            RLock lock = redissonClient.getLock(lockKey);
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (Throwable e) {
            String msg = String.format("UNLOCK FAILED: key=%s", lockKey);
            throw new IllegalStateException(msg, e);
        }
    }

}