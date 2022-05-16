package com.core.inter;

import com.annotation.RedisLock;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @Author tingmailang
 */
public interface RedisLockService {

    String getKeyWithThreadLocal(ProceedingJoinPoint joinPoint, RedisLock redisLock);


    String getKeyWithParam(ProceedingJoinPoint joinPoint, RedisLock redisLock);

}
