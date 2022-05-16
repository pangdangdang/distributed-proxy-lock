package com.core;

import com.annotation.RedisLock;
import com.core.inter.RedisLockService;
import com.enums.RedisLockSuffixKeyTypeEnum;
import com.exception.RedisLockException;
import com.util.RedisLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 分布式锁，切面处理类
 */
@Slf4j
@Aspect
@Component
public class RedisLockAspect {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisLockService redisLockService;

    @Pointcut("@annotation(com.annotation.RedisLock)")
    public void lockPointCut() {

    }

    @Around("lockPointCut() && @annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        String lockKey;
        try {
            RedisLockSuffixKeyTypeEnum suffixKeyTypeEnum = RedisLockSuffixKeyTypeEnum.of(redisLock.suffixKeyTypeEnum());
            switch (suffixKeyTypeEnum) {
                case PARAM:
                    lockKey = redisLockService.getKeyWithParam(joinPoint, redisLock);
                    break;
                case NO_SUFFIX:
                    lockKey = redisLock.key();
                    break;
                case THREAD_LOCAL:
                    lockKey = redisLockService.getKeyWithThreadLocal(joinPoint, redisLock);
                    break;
                default:
                    throw new RedisLockException("未知后缀获取类型" + redisLock.suffixKeyTypeEnum());
            }
        } catch (Exception e) {
            throw new RedisLockException("获取后缀key失败" + e);
        }

        log.debug("开始代理加锁:{}", lockKey);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(redisLock.waitOut(), redisLock.executeOut(), redisLock.timeUnit())) {
                log.debug("代理加锁成功:{}", lockKey);
                return joinPoint.proceed();
            } else {
                log.debug("代理加锁失败:{}", lockKey);
            }
        } catch (InterruptedException e) {
            log.error("获取代理锁异常:{}", e);
            throw e;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("代理解锁:{}", lockKey);
            }
            //如果方法注解中开启自动清除，就去除
            if (redisLock.atuoRemove()) {
                RedisLockUtil.remove();
                log.debug("自动清除RedisLockUtil:{}", lockKey);
            }
        }
        return null;
    }

}