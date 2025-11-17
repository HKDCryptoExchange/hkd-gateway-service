package com.hkd.gateway.service;

import com.hkd.gateway.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT Service
 * 负责JWT Token的生成和验证
 *
 * @author HKD Team
 */
@Service
@Slf4j
public class JwtService {

    @Value("${hkd.jwt.secret}")
    private String secret;

    @Value("${hkd.jwt.access-token-expire}")
    private long accessTokenTtl;

    @Value("${hkd.jwt.refresh-token-expire}")
    private long refreshTokenTtl;

    /**
     * 验证Token
     *
     * @param token JWT Token
     * @return Claims 对象
     * @throws AuthException 如果Token无效或过期
     */
    public Claims validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            log.warn("JWT Token已过期: {}", e.getMessage());
            throw new AuthException("Token已过期");
        } catch (JwtException e) {
            log.warn("JWT Token无效: {}", e.getMessage());
            throw new AuthException("Token无效");
        } catch (Exception e) {
            log.error("JWT Token验证失败", e);
            throw new AuthException("Token验证失败");
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public String getUserId(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }

    /**
     * 从Token中获取用户邮箱
     *
     * @param token JWT Token
     * @return 用户邮箱
     */
    public String getUserEmail(String token) {
        Claims claims = validateToken(token);
        return claims.get("email", String.class);
    }
}
