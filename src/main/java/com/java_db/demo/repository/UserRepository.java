package com.java_db.demo.repository;

import com.java_db.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 * 继承 JpaRepository 自动获得 CRUD 操作
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * 根据用户名查询用户（用于登录验证）
     * Spring Data JPA 自动解析方法名生成查询：
     * SELECT * FROM users WHERE username = ?
     * 
     * @param username 用户名
     * @return Optional<User> 如果存在返回用户，否则返回空
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据手机号查询用户
     * 可用于注册时检查手机号是否已存在
     * 
     * @param phone 手机号
     * @return Optional<User> 如果存在返回用户，否则返回空
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * 检查用户名是否已存在
     * 
     * @param username 用户名
     * @return true 如果存在，false 如果不存在
     */
    boolean existsByUsername(String username);
}
