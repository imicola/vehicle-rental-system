package com.java_db.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 * 对应数据库表: payments
 * 
 * 支付方式: Alipay, WeChat, Card 等
 * 支付类型: Deposit(押金), Final(尾款), Penalty(罚金)
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 关联订单
     * 多对一关联 orders 表
     * 删除订单时级联删除支付记录 (ON DELETE CASCADE)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payment_order"))
    private Order order;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 支付方式
     * 如: Alipay, WeChat, Card
     */
    @Column(name = "pay_method", length = 20)
    private String payMethod;

    /**
     * 支付类型
     * Deposit: 押金
     * Final: 尾款
     * Penalty: 罚金
     */
    @Column(name = "pay_type", length = 20)
    private String payType;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (payTime == null) {
            payTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
