package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.service.impl.RedisExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisTestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisExampleService redisExampleService;

    @PostMapping("/set")
    public String setValue(@RequestParam String key, @RequestParam String value) {
        redisExampleService.setValue(key, value);
        return "Kaydedildi!";
    }

    @GetMapping("/get")
    public String getValue(@RequestParam String key) {
        String value = redisExampleService.getValue(key);
        return value != null ? value : "Bulunamadı!";
    }

    @PostMapping("/hash/set")
    public String setHashValue(@RequestParam String key, @RequestParam String field, @RequestParam String value) {
        redisTemplate.opsForHash().put(key, field, value);
        return "Hash kaydedildi!";
    }

    @GetMapping("/hash/get")
    public String getHashValue(@RequestParam String key, @RequestParam String field) {
        Object value = redisTemplate.opsForHash().get(key, field);
        return value != null ? value.toString() : "Bulunamadı!";
    }

    @GetMapping("/hash/getAll")
    public Map<Object, Object> getAllHashValues(@RequestParam String key) {
        return redisTemplate.opsForHash().entries(key);
    }
} 