package com.java_db.demo.service;

import com.java_db.demo.entity.User;
import com.java_db.demo.exception.ResourceNotFoundException;
import com.java_db.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户管理服务
 * 处理用户信息管理等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 根据 ID 查询用户
     * 
     * @param userId 用户 ID
     * @return 用户信息
     */
    @Transactional(readOnly = true)
    public User findById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }
    
    /**
     * 查询所有用户（管理员功能）
     * 
     * @return 所有用户列表
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * 修改密码
     * 
     * @param userId 用户 ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Transactional
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = findById(userId);
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResourceNotFoundException("旧密码错误");
        }
        
        // 加密新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * 更新用户信息
     * 
     * @param userId 用户 ID
     * @param phone 新手机号
     */
    @Transactional
    public User updateUserInfo(Integer userId, String phone) {
        User user = findById(userId);
        user.setPhone(phone);
        return userRepository.save(user);
    }
}
