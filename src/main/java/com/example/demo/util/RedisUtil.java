package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import jakarta.annotation.Resource;
import java.util.Objects;

/**
 * Redis 工具类，封装常用操作
 */
@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Resource
    private JedisPool jedisPool;

    /**
     * 设置键值（如果不存在），并设置超时时间
     *
     * @param key     键
     * @param value   值（唯一标识）
     * @param timeout 超时时间（秒）
     * @return 是否设置成功
     */
    public boolean setIfAbsent(String key, String value, int timeout) {
        try (Jedis jedis = jedisPool.getResource()) {
            SetParams params = new SetParams().nx().ex(timeout); // 设置 NX 和 EX 参数
            String result = jedis.set(key, value, params);
            return "OK".equals(result);
        } catch (Exception e) {
            logger.error("Redis setIfAbsent 操作失败: key={}, 错误信息={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 延长锁的过期时间
     *
     * @param key     键
     * @param value   值（唯一标识）
     * @param timeout 新的过期时间（秒）
     * @return 是否续期成功
     */
    public boolean renewLock(String key, String value, int timeout) {
        try (Jedis jedis = jedisPool.getResource()) {
            // 仅当锁的值匹配时，才延长过期时间
            if (Objects.equals(jedis.get(key), value)) {
                jedis.expire(key, timeout);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Redis renewLock 操作失败: key={}, 错误信息={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除键（仅当值匹配时）
     *
     * @param key   键
     * @param value 值（唯一标识）
     */
    public void delete(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            // 仅当锁的值匹配时，才删除锁
            if (Objects.equals(jedis.get(key), value)) {
                jedis.del(key);
            }
        } catch (Exception e) {
            logger.error("Redis delete 操作失败: key={}, 错误信息={}", key, e.getMessage(), e);
        }
    }
}