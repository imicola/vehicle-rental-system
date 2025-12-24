package com.java_db.demo.controller;

import com.java_db.demo.dto.LoginRequest;
import com.java_db.demo.dto.LoginResponse;
import com.java_db.demo.dto.RegisterDTO;
import com.java_db.demo.entity.User;
import com.java_db.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录、注册等认证相关请求
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证接口", description = "用户登录、注册等认证相关接口")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户登录
     * 
     * @param request 登录请求（用户名和密码）
     * @return 登录响应（JWT Token 和用户信息，包含 role 字段）
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户名和密码，返回 JWT Token 和用户信息（含 role 字段供前端判断身份）")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 用户注册
     * 
     * @param registerDTO 注册信息
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户，密码将使用 BCrypt 加密存储")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterDTO registerDTO) {
        User user = authService.register(registerDTO);
        return ResponseEntity.ok(user);
    }
}
