package com.hkd.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkd.gateway.service.TokenBucketRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 限流过滤器
 * 实现多维度限流：IP限流、用户限流、API限流
 *
 * @author HKD Team
 */
@Component
@Slf4j
public class RateLimitFilter implements GlobalFilter, Ordered {

    @Autowired
    private TokenBucketRateLimiter rateLimiter;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${hkd.rate-limit.ip.capacity}")
    private int ipCapacity;

    @Value("${hkd.rate-limit.ip.refill-rate}")
    private int ipRefillRate;

    @Value("${hkd.rate-limit.user.default-capacity}")
    private int userDefaultCapacity;

    @Value("${hkd.rate-limit.api.trading-capacity}")
    private int tradingCapacity;

    @Value("${hkd.rate-limit.api.market-capacity}")
    private int marketCapacity;

    @Value("${hkd.rate-limit.api.default-capacity}")
    private int apiDefaultCapacity;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 1. IP限流
        String ip = getClientIp(request);
        if (!rateLimiter.tryAcquire("ratelimit:ip:" + ip, ipCapacity, ipRefillRate)) {
            log.warn("IP限流触发: ip={}, path={}", ip, path);
            return tooManyRequests(exchange, "请求过于频繁，请稍后再试");
        }

        // 2. 用户限流
        String userId = request.getHeaders().getFirst("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            int userLimit = getUserRateLimit(userId);
            if (!rateLimiter.tryAcquire("ratelimit:user:" + userId, userLimit, userLimit)) {
                log.warn("用户限流触发: userId={}, path={}", userId, path);
                return tooManyRequests(exchange, "操作过于频繁，请稍后再试");
            }

            // 3. API限流
            int apiLimit = getApiRateLimit(path);
            String apiKey = "ratelimit:api:" + path + ":" + userId;
            if (!rateLimiter.tryAcquire(apiKey, apiLimit, apiLimit)) {
                log.warn("API限流触发: userId={}, path={}, limit={}", userId, path, apiLimit);
                return tooManyRequests(exchange, "该接口调用频率超限，请稍后再试");
            }
        }

        return chain.filter(exchange);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(ServerHttpRequest request) {
        // 先从X-Forwarded-For获取（通过代理的情况）
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 多次代理的情况，取第一个IP
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index).trim();
            }
            return ip.trim();
        }

        // X-Real-IP
        ip = request.getHeaders().getFirst("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        // 直接从RemoteAddress获取
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (remoteAddress != null && remoteAddress.getAddress() != null) {
            return remoteAddress.getAddress().getHostAddress();
        }

        return "unknown";
    }

    /**
     * 获取用户限流配置
     * TODO: 从Redis或数据库获取用户等级对应的限流配置
     */
    private int getUserRateLimit(String userId) {
        // 这里可以根据用户等级返回不同的限流值
        // 示例：VIP用户更高的限流
        return userDefaultCapacity;
    }

    /**
     * 获取API限流配置
     */
    private int getApiRateLimit(String path) {
        // 交易API更严格的限流
        if (path.startsWith("/api/v1/orders/") || path.startsWith("/api/v1/trading/")) {
            return tradingCapacity;
        }
        // 行情API较宽松的限流
        else if (path.startsWith("/api/v1/market/")) {
            return marketCapacity;
        }
        // 默认限流
        return apiDefaultCapacity;
    }

    /**
     * 返回429 Too Many Requests响应
     */
    private Mono<Void> tooManyRequests(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().set("X-RateLimit-Retry-After", "1");

        Map<String, Object> error = new HashMap<>();
        error.put("code", "RATE_LIMIT_EXCEEDED");
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(error);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("序列化错误响应失败", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -90;  // 在JWT验证之后执行
    }
}
