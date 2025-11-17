package com.hkd.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 熔断器配置
 * 使用Resilience4j实现熔断降级
 *
 * @author HKD Team
 */
@Configuration
public class CircuitBreakerConfig {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                        // 熔断器开启阈值: 50%失败率
                        .failureRateThreshold(50)
                        // 慢调用阈值: 50%
                        .slowCallRateThreshold(50)
                        // 慢调用判定时间: 2秒
                        .slowCallDurationThreshold(Duration.ofSeconds(2))
                        // 滑动窗口: 10次调用
                        .slidingWindowSize(10)
                        .slidingWindowType(SlidingWindowType.COUNT_BASED)
                        // 半开状态允许的调用数
                        .permittedNumberOfCallsInHalfOpenState(3)
                        // 熔断器打开后等待时间: 30秒
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        // 最小调用次数（达到这个次数后才开始计算失败率）
                        .minimumNumberOfCalls(5)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        // 超时时间: 3秒
                        .timeoutDuration(Duration.ofSeconds(3))
                        .build())
                .build());
    }
}
