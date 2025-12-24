package com.java_db.demo.repository;

import com.java_db.demo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 支付记录数据访问层
 * 继承 JpaRepository 自动获得 CRUD 操作
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    /**
     * 根据订单 ID 查询所有支付记录
     * Spring Data JPA 自动解析方法名生成查询
     * 
     * @param orderId 订单 ID
     * @return 支付记录列表
     */
    List<Payment> findByOrderId(Integer orderId);
}
