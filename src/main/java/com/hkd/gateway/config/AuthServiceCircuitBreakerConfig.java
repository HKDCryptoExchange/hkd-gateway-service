package com.hkd.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Auth Service 熔断器配置
 *
 * 为 auth-service 的 gRPC 调用提供专门的熔断保护
 * auth-service 是认证中心，要求高可用性
 *
 * @author HKD Team
 */
@Configuration
public class AuthServiceCircuitBreakerConfig {

    @Bean
    public CircuitBreaker authServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                // 30% 失败率触发熔断（auth很重要，阈值较低）
                .failureRateThreshold(30)
                // 慢调用阈值：50%
                .slowCallRateThreshold(50)
                // 慢调用判定时间：200ms
                .slowCallDurationThreshold(Duration.ofMillis(200))
                // 熔断器打开后等待时间：10秒
                .waitDurationInOpenState(Duration.ofSeconds(10))
                // 滑动窗口：100次调用
                .slidingWindowSize(100)
                // 最小调用次数（达到这个次数后才开始计算失败率）
                .minimumNumberOfCalls(10)
                .build();

        return registry.circuitBreaker("auth-service", config);
    }
}
