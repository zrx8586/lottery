package com.example.demo.lottery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author long_w
 */
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 设置键值对
    @PostMapping("/set")
    public String setKeyValue(@RequestParam String key, @RequestParam String value) {
        redisTemplate.opsForValue().set(key, value);
        return "Key-Value pair set successfully!";
    }

    // 获取值
    @GetMapping("/get")
    public String getValue(@RequestParam String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? value : "Key not found!";
    }

    // 删除键
    @DeleteMapping("/delete")
    public String deleteKey(@RequestParam String key) {
        Boolean result = redisTemplate.delete(key);
        return result != null && result ? "Key deleted successfully!" : "Key not found!";
    }
}