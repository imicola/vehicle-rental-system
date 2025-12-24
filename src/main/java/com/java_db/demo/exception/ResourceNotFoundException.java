package com.java_db.demo.exception;

/**
 * 资源未找到异常
 * 用于查询数据不存在时的异常处理
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
