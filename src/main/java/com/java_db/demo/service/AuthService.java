package com.java_db.demo.service;

import com.java_db.demo.dto.LoginRequest;
import com.java_db.demo.dto.LoginResponse;
import com.java_db.demo.dto.RegisterDTO;
import com.java_db.demo.entity.User;
import com.java_db.demo.exception.AuthException;
import com.java_db.demo.exception.BusinessException;
import com.java_db.demo.repository.UserRepository;
import com.java_db.demo.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户认证服务
 * 处理登录、注册等认证相关业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    
    /**
     * 用户登录
     * 验证用户名和密码，成功后返回 JWT Token
     * 
     * @param request 登录请求（用户名和密码）
     * @return 登录响应（包含 Token 和用户信息）
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthException("用户名或密码错误"));
        
        // 验证密码（使用 BCrypt）
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("用户名或密码错误");
        }
        
        // 生成 JWT Token
        String token = jwtProvider.generateToken(user.getId(), user.getRole());
        
        // 返回登录响应（包含 role 字段供前端判断用户身份）
        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getPhone()
        );
    }
    
    /**
     * 用户注册
     * 检查用户名是否已存在，使用 BCrypt 加密密码后保存
     * 
     * @param registerDTO 注册信息
     * @return 注册成功的用户信息
     */
    @Transactional
    public User register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (registerDTO.getPhone() != null && 
            userRepository.findByPhone(registerDTO.getPhone()).isPresent()) {
            throw new BusinessException("手机号已被注册");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        // 使用 BCrypt 加密密码
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhone(registerDTO.getPhone());
        user.setRole(0); // 默认为普通客户
        
        return userRepository.save(user);
    }
}
