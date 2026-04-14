package org.example.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis配置类
 * 用于配置RedisTemplate和Spring Cache，支持Redis缓存注解的使用
 * 继承CachingConfigurerSupport以支持Spring Cache相关配置
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 配置RedisTemplate Bean
     * RedisTemplate是Spring Data Redis提供的核心操作类，用于执行Redis操作
     *
     * @param connectionFactory Redis连接工厂，由Spring自动注入
     * @return 配置好的RedisTemplate实例
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        // 设置key的序列化方式为字符串序列化
        // 这样Redis中的key就是可读的字符串，而不是乱码
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 设置value的序列化方式为JSON序列化
        // 使用GenericJackson2JsonRedisSerializer可以将对象自动序列化为JSON字符串
        // 支持复杂对象的存储和读取，包括嵌套对象、集合等
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 设置Hash结构中key的序列化方式为字符串序列化
        // Hash是Redis中的一种数据结构，类似于Java的Map
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 设置Hash结构中value的序列化方式为JSON序列化
        // 这样Hash中的value也可以存储复杂对象
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 设置Redis连接工厂
        // RedisTemplate需要通过连接工厂来与Redis服务器建立连接
        redisTemplate.setConnectionFactory(connectionFactory);

        return redisTemplate;
    }

    /**
     * 配置CacheManager
     * CacheManager是Spring Cache的核心接口，用于管理缓存
     * 这里使用RedisCacheManager实现，将缓存数据存储在Redis中
     *
     * @param connectionFactory Redis连接工厂
     * @return 配置好的RedisCacheManager实例
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

}