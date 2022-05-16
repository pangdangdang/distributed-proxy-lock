package com.core;

import com.annotation.RedisLock;
import com.core.inter.RedisLockService;
import com.exception.RedisLockException;
import com.util.RedisLockUtil;
import jodd.util.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class RedisLockKeyServiceImpl implements RedisLockService {

    @Override
    public String getKeyWithThreadLocal(ProceedingJoinPoint joinPoint, RedisLock redisLock) {
        String lockKey = redisLock.key();
        if (StringUtil.isBlank(RedisLockUtil.get())) {
            throw new RedisLockException("RedisLockUtil为空");
        }
        return lockKey;
    }


    @Override
    public String getKeyWithParam(ProceedingJoinPoint joinPoint, RedisLock redisLock) {
        String objectName = redisLock.objectName();
        if (StringUtil.isBlank(objectName)) {
            throw new RedisLockException("objectName为空");
        }
        String paramName = redisLock.paramName();
        Object[] args = joinPoint.getArgs();
        String[] objectNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Map<String, Object> objectHashMap = new HashMap<>();
        for (int i = 0; i < objectNames.length; i++) {
            objectHashMap.put(objectNames[i], args[i]);
        }
        if (!objectHashMap.containsKey(objectName)) {
            throw new RedisLockException("入参不包含该对象" + objectName);
        }
        Object o = objectHashMap.get(objectName);
        if (StringUtil.isBlank(paramName)) {
            return redisLock.key() + o.toString();
        }
        String lockKey = redisLock.key() + CommonUtil.getFieldValueByName(paramName, o);
        return lockKey;
    }
}