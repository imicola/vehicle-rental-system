package com.java_db.demo.exception;

/**
 * 认证异常
 * 用于登录、注册等认证场景的异常处理
 */
public class AuthException extends RuntimeException {
    
    public AuthException(String message) {
        super(message);
    }
    
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
