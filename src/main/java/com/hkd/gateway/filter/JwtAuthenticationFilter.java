package com.hkd.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkd.auth.grpc.ValidateTokenResponse;
import com.hkd.gateway.client.AuthServiceClient;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT认证过滤器 - 通过gRPC调用auth-service验证Token
 *
 * 架构原则：
 * - Gateway 不保存 JWT 密钥
 * - 所有认证逻辑由 auth-service 集中管理
 * - Token 黑名单实时生效
 * - 符合微服务单一职责原则
 *
 * @author HKD Team
 */
@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("#{'${hkd.auth.whitelist}'.split(',')}")
    private List<String> whitelist;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        log.debug("JWT认证过滤器: path={}", path);

        // 1. 白名单路径跳过验证
        if (isWhitelisted(path)) {
            log.debug("路径在白名单中，跳过JWT验证: {}", path);
            return chain.filter(exchange);
        }

        // 2. 提取Token
        String token = extractToken(request);
        if (token == null || token.isEmpty()) {
            log.warn("请求缺少Authorization Header: path={}", path);
            return unauthorized(exchange, "缺少认证信息");
        }

        // 3. 🔥 调用 auth-service gRPC 验证 Token
        ValidateTokenResponse validationResult;
        try {
            validationResult = authServiceClient.validateToken(token);
        } catch (Exception e) {
            // 熔断器打开或auth-service不可用
            log.error("无法验证Token（auth-service不可用）: {}", e.getMessage());
            return unauthorized(exchange, "认证服务暂时不可用，请稍后再试");
        }

        if (!validationResult.getValid()) {
            log.warn("Token验证失败: path={}", path);
            return unauthorized(exchange, "认证令牌无效或已过期");
        }

        // 4. Token有效，将用户信息注入到请求Header
        String userId = validationResult.getUserId();
        String email = validationResult.getEmail();
        String username = validationResult.getUsername();
        List<String> roles = validationResult.getRolesList();

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", userId)
                .header("X-User-Email", email != null ? email : "")
                .header("X-Username", username != null ? username : "")
                .header("X-User-Roles", roles != null && !roles.isEmpty() ? String.join(",", roles) : "")
                .build();

        log.debug("用户认证成功: userId={}, username={}, path={}", userId, username, path);

        // 5. 继续执行后续过滤器
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    /**
     * 从请求中提取Token
     */
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhitelisted(String path) {
        return whitelist.stream()
                .anyMatch(pattern -> pathMatches(path, pattern));
    }

    /**
     * 路径匹配（支持通配符）
     */
    private boolean pathMatches(String path, String pattern) {
        // 简单的通配符匹配
        pattern = pattern.trim();
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        } else if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return path.startsWith(prefix) && !path.substring(prefix.length()).contains("/");
        } else {
            return path.equals(pattern);
        }
    }

    /**
     * 返回401 Unauthorized响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> error = new HashMap<>();
        error.put("code", "UNAUTHORIZED");
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
        return -100;  // 高优先级，在其他过滤器之前执行
    }
}
