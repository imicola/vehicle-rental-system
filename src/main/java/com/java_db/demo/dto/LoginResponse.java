package com.java_db.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private Integer userId;
    private String username;
    private Integer role; // 0:客户, 1:管理员, 2:门店员工
    private String phone;
    
    public LoginResponse(String token, Integer userId, String username, Integer role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
}
