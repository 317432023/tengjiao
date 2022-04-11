package com.tengjiao.part.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

/**
 * Redis配置.
 * 使用@EnableCaching开启声明式缓存支持. 之后就可以使用 @Cacheable/@CachePut/@CacheEvict 注解缓存数据.
 * 0 @EnableCaching开启声明式缓存支持，这样就可以使用基于注解的缓存技术。注解缓存是一个对缓存使用的抽象，通过在代码中添加下面的一些注解，达到缓存的效果。
 *
 * 1 @Cacheable：在方法执行前Spring先查看缓存中是否有数据，如果有数据，则直接返回缓存数据；没有则调用方法并将方法返回值放进缓存。
 *
 * 2 @CachePut：将方法的返回值放到缓存中。
 *
 * 3 @CacheEvict：删除缓存中的数据。
 */
@Configuration
@EnableCaching
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig extends CachingConfigurerSupport {
    private Logger log = LogManager.getLogger(RedisConfig.class);

    @Bean(name = "jsonRedisSerializer")
    public Jackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(new ObjectMapper()
          .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
          .activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL)
        );
        return jackson2JsonRedisSerializer;
    }

    /**
     * 覆盖默认配置 RedisTemplate，使用 String 类型作为key，设置key/value的序列化规则
     *
     * Redis 默认配置了 RedisTemplate 和 StringRedisTemplate ，其中RedisTemplate使用的序列化规则是 JdkSerializationRedisSerializer，缓存到redis后，数据都变成了\x0..，非常不易于阅读。
     *
     * 因此，重新配置RedisTemplate，使用 Jackson2JsonRedisSerializer 来序列化 Key 和 Value。同时，增加HashOperations、ValueOperations等Redis数据结构相关的操作，这样比较方便使用
     */
    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory,
                                                       @Qualifier("jsonRedisSerializer") Jackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        log.info("====================================RedisConfiguration.redisTemplate====================================");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置key的序列化和反序列化规则 为 StringRedisSerializer
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置value的序列化和反序列化规则 为 Jackson2JsonRedisSerializer
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory,
                                     @Qualifier("jsonRedisSerializer") Jackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
          .entryTtl(Duration.ofMinutes(10L))// 设置缓存的默认过期时间，也是使用Duration设置
          .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
          .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
          .disableCachingNullValues()// 不缓存空值
          ;

        return RedisCacheManager.builder(factory)
          .cacheDefaults(config)
          .build();

        /*
        // 设置一个初始化的缓存空间set集合
        Set<String> cacheNames =  new HashSet<>();
        cacheNames.add("my-redis-cache1");
        cacheNames.add("my-redis-cache2");

        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("my-redis-cache1", config);
        configMap.put("my-redis-cache2", config.entryTtl(Duration.ofMinutes(2L)));

        // 使用自定义的缓存配置初始化一个cacheManager
        return RedisCacheManager.builder(factory)
          .initialCacheNames(cacheNames)  // 注意这两句的调用顺序，一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
          .withInitialCacheConfigurations(configMap)
          .build();
        */
    }

}