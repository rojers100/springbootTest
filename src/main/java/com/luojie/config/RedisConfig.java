package com.luojie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxTotal;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private Duration maxWaitMillis;

    /**
     * Redis 连接工厂配置
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        if (redisHost.contains(",")) {
            // 配置集群 Redis 连接工厂
            return clusterConnectionFactory();
        } else {
            //  配置单机 Redis 连接工厂
            return standaloneConnectionFactory();
        }
    }

    /**
     * 当redis为单机情况时
     * @return
     */
    private RedisConnectionFactory standaloneConnectionFactory() {
        // RedisStandaloneConfiguration 设置单机 Redis 配置
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setPassword(redisPassword);

        // JedisClientConfiguration 配置 Jedis 连接
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Duration.ofMillis(3500)); // connection timeout
        // JedisPoolConfig 配置连接池参数
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis.toMillis());

        jedisClientConfiguration.usePooling().poolConfig(poolConfig);
        // 使用 JedisConnectionFactory 创建连接工厂
        return new JedisConnectionFactory(config, jedisClientConfiguration.build());
    }

    private RedisConnectionFactory clusterConnectionFactory() {
        // RedisClusterConfiguration 设置集群 Redis 配置
        RedisClusterConfiguration config = new RedisClusterConfiguration();
        for (String node : redisHost.split(",")) {
            String[] parts = node.split(":");
            config.clusterNode(parts[0], Integer.parseInt(parts[1]));
        }
        config.setPassword(redisPassword);

        // JedisClientConfiguration 配置 Jedis 连接
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Duration.ofMillis(3500)); // connection timeout

        // JedisPoolConfig 配置连接池参数
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis.toMillis());

        jedisClientConfiguration.usePooling().poolConfig(poolConfig);

        // 使用 JedisConnectionFactory 创建连接工厂
        return new JedisConnectionFactory(config, jedisClientConfiguration.build());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 使用StringRedisSerializer序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 使用GenericJackson2JsonRedisSerializer序列化和反序列化redis的value值
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
