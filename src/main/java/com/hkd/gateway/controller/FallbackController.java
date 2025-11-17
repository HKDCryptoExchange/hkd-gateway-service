package com.hkd.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 熔断降级处理器
 * 当后端服务不可用时返回友好的错误信息
 *
 * @author HKD Team
 */
@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    /**
     * 订单服务降级
     */
    @GetMapping("/order")
    public Mono<ResponseEntity<Map<String, Object>>> orderFallback() {
        log.warn("订单服务熔断降级");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createErrorResponse("ORDER_SERVICE_UNAVAILABLE", "订单服务暂时不可用，请稍后再试")));
    }

    /**
     * 交易服务降级
     */
    @GetMapping("/trading")
    public Mono<ResponseEntity<Map<String, Object>>> tradingFallback() {
        log.warn("交易服务熔断降级");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createErrorResponse("TRADING_SERVICE_UNAVAILABLE", "交易服务暂时不可用，请稍后再试")));
    }

    /**
     * 默认降级处理
     */
    @GetMapping("/default")
    public Mono<ResponseEntity<Map<String, Object>>> defaultFallback() {
        log.warn("默认服务熔断降级");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createErrorResponse("SERVICE_UNAVAILABLE", "服务暂时不可用，请稍后再试")));
    }

    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String code, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
