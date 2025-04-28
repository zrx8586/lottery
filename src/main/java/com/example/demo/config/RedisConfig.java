package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis 配置类
 */
@Configuration
public class RedisConfig {

    // Redis 主机地址
    @Value("${spring.data.redis.host}")
    private String redisHost;

    // Redis 端口号
    @Value("${spring.data.redis.port}")
    private int redisPort;

    // Redis 密码
    @Value("${spring.data.redis.password}")
    private String redisPassword;

    // Redis 连接池配置 - 最大连接
    @Value("${spring.data.redis.lettuce.pool.max-active}")
    private int maxTotal;

    // Redis 连接池配置 - 最大空闲连接
    @Value("${spring.data.redis.lettuce.pool.max-idle}")
    private int maxIdle;

    // Redis 连接池配置 - 最小空闲连接
    @Value("${spring.data.redis.lettuce.pool.min-idle}")
    private int minIdle;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setJmxEnabled(false); // 禁用 JMX 注册
        // 添加 Redis 认证信息
        return new JedisPool(poolConfig, redisHost, redisPort, 2000, redisPassword);
    }
}