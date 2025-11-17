package com.hkd.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 令牌桶限流器
 * 使用Redis + Lua脚本实现分布式限流
 *
 * @author HKD Team
 */
@Component
@Slf4j
public class TokenBucketRateLimiter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Lua脚本：令牌桶算法
     */
    private static final String LUA_SCRIPT =
            "local key = KEYS[1]\n" +
            "local capacity = tonumber(ARGV[1])\n" +
            "local refill_rate = tonumber(ARGV[2])\n" +
            "local now = tonumber(ARGV[3])\n" +
            "\n" +
            "local bucket = redis.call('HMGET', key, 'tokens', 'last_refill')\n" +
            "local tokens = tonumber(bucket[1]) or capacity\n" +
            "local last_refill = tonumber(bucket[2]) or now\n" +
            "\n" +
            "-- 计算应补充的令牌数\n" +
            "local time_passed = now - last_refill\n" +
            "local refill = math.floor(time_passed * refill_rate)\n" +
            "tokens = math.min(capacity, tokens + refill)\n" +
            "\n" +
            "-- 尝试获取1个令牌\n" +
            "if tokens >= 1 then\n" +
            "    tokens = tokens - 1\n" +
            "    redis.call('HMSET', key, 'tokens', tokens, 'last_refill', now)\n" +
            "    redis.call('EXPIRE', key, 60)\n" +
            "    return 1  -- 允许\n" +
            "else\n" +
            "    return 0  -- 拒绝\n" +
            "end";

    /**
     * 尝试获取令牌
     *
     * @param key         限流Key
     * @param capacity    令牌桶容量
     * @param refillRate  每秒补充令牌数
     * @return true if 获取成功, false otherwise
     */
    public boolean tryAcquire(String key, int capacity, int refillRate) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(LUA_SCRIPT);
        script.setResultType(Long.class);

        try {
            Long result = redisTemplate.execute(
                    script,
                    Collections.singletonList(key),
                    String.valueOf(capacity),
                    String.valueOf(refillRate),
                    String.valueOf(System.currentTimeMillis() / 1000.0)
            );

            boolean allowed = result != null && result == 1;
            if (!allowed) {
                log.debug("限流触发: key={}, capacity={}, refillRate={}", key, capacity, refillRate);
            }
            return allowed;

        } catch (Exception e) {
            log.error("限流检查失败: key={}", key, e);
            // 出错时默认允许通过（Fail Open策略）
            return true;
        }
    }
}
