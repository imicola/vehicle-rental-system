package com.java_db.demo.exception;

/**
 * 业务异常
 * 用于一般业务逻辑的异常处理
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
