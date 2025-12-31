package com.java_db.demo.repository;

import com.java_db.demo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
    
    // ==================== 报表统计查询方法 ====================
    
    /**
     * 按支付类型统计指定时间范围内的金额
     */
    @Query("SELECT p.payType as payType, COALESCE(SUM(p.amount), 0) as totalAmount FROM Payment p " +
           "WHERE p.payTime BETWEEN :startDate AND :endDate " +
           "GROUP BY p.payType")
    List<Object[]> sumAmountByPayTypeBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * 统计指定时间范围内的总收入
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.payTime BETWEEN :startDate AND :endDate")
    Double sumTotalAmountBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * 按支付方式统计金额
     */
    @Query("SELECT p.payMethod as payMethod, COALESCE(SUM(p.amount), 0) as totalAmount FROM Payment p " +
           "WHERE p.payTime BETWEEN :startDate AND :endDate " +
           "GROUP BY p.payMethod")
    List<Object[]> sumAmountByPayMethodBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
}

