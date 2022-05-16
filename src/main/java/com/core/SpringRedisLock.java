package com.core;

import com.annotation.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * spring Redis 加锁
 *
 * @author 宋昊
 */
@Slf4j
@Component
public class SpringRedisLock {

    @Resource
    private RedisTemplate redisTemplate;

    public Object process(String lockKey, ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        try {
            if (redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey)) {
                redisTemplate.expire(lockKey, redisLock.executeOut(), redisLock.timeUnit());
                log.debug("代理加锁成功:{}", lockKey);
                return joinPoint.proceed();
            } else {
                log.debug("代理加锁失败:{}", lockKey);
            }
        } catch (InterruptedException e) {
            log.error("获取代理锁异常:{}", e);
            throw e;
        } finally {
            redisTemplate.delete(lockKey);
        }
        return null;

    }

}
