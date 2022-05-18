package com.annotation;

import com.core.RedisLockCommonUtil;
import com.enums.RedisLockSuffixKeyTypeEnum;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * redis锁注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    String key();
    long waitOut() default 60;
    long executeOut() default 60;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    boolean atuoRemove() default true;
    String suffixKeyTypeEnum() default RedisLockCommonUtil.NO_SUFFIX;
    String objectName() default "";
    String paramName() default "";
    String redisEnum() default RedisLockCommonUtil.REDISSON;
}