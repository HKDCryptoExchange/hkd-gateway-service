package com.hkd.gateway.exception;

/**
 * 认证异常
 *
 * @author HKD Team
 */
public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
