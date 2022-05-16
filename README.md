# redislock

#### 介绍

基于redis的分布式代理锁，动态的锁后缀采用ThreadLocal或者参数名获取

#### 软件架构

基于Spring架构，默认使用redisson链接redis，可以更改配置使用其他redis工具

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

1.  无后缀

    @RedisLock(key = "SHOP_LOCK_KEY")   
    public void test(ShopChainDTO shopChainDTO) {   
        for (int i = 0; i < 6; i++) {   
            log.info("测试加锁:{}", LockUtil.get() + i);    
        }   
    }   
    
2.  参数中获取

    ①从某个入参对象的某个参数获取
    
        @RedisLock(key = "SHOP_LOCK_KEY", 
            suffixKeyTypeEnum = "param"
            objectName = "shopChainDTO",
            paramName = "shopId")   
        public void test(ShopChainDTO shopChainDTO) {   
            for (int i = 0; i < 6; i++) {   
                log.info("测试加锁:{}", LockUtil.get() + i);    
            }   
        }   
        
    ②从某个入参对象获取
    
        @RedisLock(key = "SHOP_LOCK_KEY", 
            objectName = "shopId")  
        public void test(LocalDateTime onlineTime, String shopId) { 
            for (int i = 0; i < 6; i++) {   
                log.info("测试加锁:{}", LockUtil.get() + i);    
            }   
        }   
        
3.  使用ThreadLocal获取

    @Slf4j  
    @Service    
    public class ShopServiceImpl implements ShopService {   
    
        @RedisLock(key = "SHOP_LOCK_KEY", 
            suffixKeyTypeEnum = "thread_local") 
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
    

