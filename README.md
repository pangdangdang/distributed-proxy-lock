# redislock

#### 介绍

分布式代理锁，动态的锁后缀采用ThreadLocal或者参数名获取 锁粒度自定义选择，目前实现基于redis，后续扩展zk等

#### 软件架构

SpringBoot，默认使用redisson链接redis，可以更改注解参数使用Spring redis工具

redis注册(redisson)：

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;
    
    @Value("${spring.redis.port}")
    private String port;
    
    @Value("${spring.redis.password}")
    private String password;
    
    @Value("${spring.redis.timeout}")
    private int timeout;
    
    @Value("${spring.redis.pool.min-idle}")
    private int minIdleSize;
    
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(StringUtils.isEmpty(password) ? null : password)
                .setTimeout(timeout)
                .setConnectionMinimumIdleSize(minIdleSize);
        return Redisson.create(config);
    }
    
}

redis注册(spring.redis)：

#### 使用说明

1.  无后缀（redisson加锁）

    @DistributedProxyLock(key = "SHOP_LOCK_KEY")   
    public void test(ShopChainDTO shopChainDTO) {   
        for (int i = 0; i < 6; i++) {   
            log.info("测试加锁:{}", LockUtil.get() + i);    
        }   
    }   
    
2.  参数中获取（redisson加锁）

    ①从某个入参对象的某个参数获取
    
        @DistributedProxyLock(key = "SHOP_LOCK_KEY", 
            suffixKeyTypeEnum = DistributedProxyLockCommonUtil.PARAM,
            objectName = "shopChainDTO",
            paramName = "shopId")   
        public void test(ShopChainDTO shopChainDTO) {   
            for (int i = 0; i < 6; i++) {   
                log.info("测试加锁:{}", LockUtil.get() + i);    
            }   
        }   
        
    ②从某个入参对象获取
    
        @DistributedProxyLock(key = "SHOP_LOCK_KEY", 
            suffixKeyTypeEnum = DistributedProxyLockCommonUtil.PARAM,
            objectName = "shopId")  
        public void test(LocalDateTime onlineTime, String shopId) { 
            for (int i = 0; i < 6; i++) {   
                log.info("测试加锁:{}", LockUtil.get() + i);    
            }   
        }   
        
3.  使用ThreadLocal获取（redisson加锁）

    @Slf4j  
    @Service    
    public class ShopServiceImpl implements ShopService {   
    
        @DistributedProxyLock(key = "SHOP_LOCK_KEY", 
            suffixKeyTypeEnum = DistributedProxyLockCommonUtil.THREAD_LOCAL) 
        public void test(ShopChainDTO shopChainDTO) {   
            for (int i = 0; i < 6; i++) {   
                log.info("测试加锁:{}", LockUtil.get() + i);    
            }   
        }   
        
    }
    
    @RestController 
    @RequestMapping("shop") 
    public class ShopController {   
        
        @Resource   
        private ShopService shopService;    

        @PostMapping("/online") 
        @MethodLogger   
        public void run(@RequestBody @Validated ShopChainDTO dto) { 
            LockUtil.set(dto.getShopId());  
            shopService.test(dto);  
        }
    }
    
4.  无后缀（Spring redis加锁）

    @DistributedProxyLock(key = "SHOP_LOCK_KEY",
        redisEnum = DistributedProxyLockCommonUtil.SPRING_REDIS)   
    public void test(ShopChainDTO shopChainDTO) {   
        for (int i = 0; i < 6; i++) {   
            log.info("测试加锁:{}", LockUtil.get() + i);    
        }   
    }   
    
5.  参数中获取（Spring redis加锁）

    ①从某个入参对象的某个参数获取
    
        @DistributedProxyLock(key = "SHOP_LOCK_KEY", 
            suffixKeyTypeEnum = DistributedProxyLockCommonUtil.PARAM,
            objectName = "shopChainDTO",
            paramName = "shopId",
            redisEnum = DistributedProxyLockCommonUtil.SPRING_REDIS)   
        public void test(ShopChainDTO shopChainDTO) {   
            for (int i = 0; i < 6; i++) {   
                log.info("测试加锁:{}", LockUtil.get() + i);    
            }   
        }   
        
    ②从某个入参对象获取
    
        @DistributedProxyLock(key = "SHOP_LOCK_KEY", 
            suffixKeyTypeEnum = DistributedProxyLockCommonUtil.PARAM,
            objectName = "shopId",
            redisEnum = DistributedProxyLockCommonUtil.SPRING_REDIS)  
        public void test(LocalDateTime onlineTime, String shopId) { 
            for (int i = 0; i < 6; i++) {   
                log.info("测试加锁:{}", LockUtil.get() + i);    
            }   
        }   
        
6.  使用ThreadLocal获取（Spring redis加锁）

    @Slf4j  
    @Service    
    public class ShopServiceImpl implements ShopService {   
    
        @DistributedProxyLock(key = "SHOP_LOCK_KEY", 
            suffixKeyTypeEnum = DistributedProxyLockCommonUtil.THREAD_LOCAL,
             redisEnum = DistributedProxyLockCommonUtil.SPRING_REDIS) 
        public void test(ShopChainDTO shopChainDTO) {   
            for (int i = 0; i < 6; i++) {   
                log.info("测试加锁:{}", LockUtil.get() + i);    
            }   
        }   
        
    }
    
    @RestController 
    @RequestMapping("shop") 
    public class ShopController {   
        
        @Resource   
        private ShopService shopService;    

        @PostMapping("/online") 
        @MethodLogger   
        public void run(@RequestBody @Validated ShopChainDTO dto) { 
            LockUtil.set(dto.getShopId());  
            shopService.test(dto);  
        }
    }
    

