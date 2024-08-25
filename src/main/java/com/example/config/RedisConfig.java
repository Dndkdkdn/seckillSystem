package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * ClassName: RedisConfig
 * Package: com.example.config
 * Description:  默认存入redis的数据是二进制的，存用户对象的话最好对其进行相应的序列化   实现序列化
 *
 * @Author YUYU
 * @Create 2024/8/5 16:06
 * @Version 1.0
 */
@Configuration
public class RedisConfig {


    @Primary
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //对redisTemplate进行配置
        //key的序列化
        redisTemplate.setKeySerializer((new StringRedisSerializer()));
        //value的序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        //hash key序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //hash value序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());


        //注入连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }


    @Bean
    public DefaultRedisScript<Long> script(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        //lock.lua脚本位置和application.yml同级目录
        redisScript.setLocation(new ClassPathResource("redisLockStock.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
