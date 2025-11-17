package com.hkd.gateway.client;

import com.hkd.auth.grpc.AuthServiceGrpc;
import com.hkd.auth.grpc.ValidateTokenRequest;
import com.hkd.auth.grpc.ValidateTokenResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Auth Service gRPC Client
 * 负责与 auth-service 通信验证 JWT Token
 * 使用熔断器保护 gRPC 调用
 *
 * @author HKD Team
 */
@Slf4j
@Service
public class AuthServiceClient {

    @GrpcClient("auth-service")
    private AuthServiceGrpc.AuthServiceBlockingStub authServiceStub;

    @Autowired
    private CircuitBreaker authServiceCircuitBreaker;

    /**
     * 验证 JWT Token（使用熔断器保护）
     *
     * @param accessToken JWT Access Token
     * @return ValidateTokenResponse 验证结果
     */
    public ValidateTokenResponse validateToken(String accessToken) {
        // 🔥 使用熔断器包装 gRPC 调用
        return authServiceCircuitBreaker.executeSupplier(() -> {
            try {
                ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                        .setAccessToken(accessToken)
                        .build();

                // 设置超时时间（100ms，因为是内网调用，要求高性能）
                ValidateTokenResponse response = authServiceStub
                        .withDeadlineAfter(100, TimeUnit.MILLISECONDS)
                        .validateToken(request);

                log.debug("Token验证结果: valid={}, userId={}",
                        response.getValid(), response.getUserId());

                return response;

            } catch (StatusRuntimeException e) {
                log.error("调用 auth-service 失败: {}", e.getStatus(), e);

                // 抛出异常让熔断器记录失败
                throw new RuntimeException("Auth service unavailable", e);

            } catch (Exception e) {
                log.error("调用 auth-service 出现异常", e);

                // 抛出异常让熔断器记录失败
                throw new RuntimeException("Auth service error", e);
            }
        });
    }
}
