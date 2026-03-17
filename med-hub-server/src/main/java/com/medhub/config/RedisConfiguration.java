package com.medhub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("RedisConfiguration.redisTemplate");
        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置 RedisConnectionFactory
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        
        // 配置 ObjectMapper 以支持 Java 8 时间类型
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        
        // 创建 JSON 序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // 设置 Key 序列化器 - 使用 String 序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置 Value 序列化器 - 使用 JSON 序列化
        redisTemplate.setValueSerializer(jsonSerializer);
        // 设置 Hash Key 序列化器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // 设置 Hash Value 序列化器
        redisTemplate.setHashValueSerializer(jsonSerializer);
        return redisTemplate;
    }
}
