package com.java_db.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 对应数据库表: orders
 * 
 * 注意: Order 是 SQL 保留字，使用 @Table 指定表名
 * 
 * 状态说明:
 * 0 - 预订
 * 1 - 使用中
 * 2 - 已还车
 * 3 - 已取消
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_no", nullable = false, unique = true, length = 64)
    private String orderNo;

    /**
     * 下单用户
     * 多对一关联 users 表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_user"))
    private User user;

    /**
     * 租赁车辆
     * 多对一关联 vehicles 表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_vehicle"))
    private Vehicle vehicle;

    /**
     * 取车门店
     * 多对一关联 stores 表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_store_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_pickup_store"))
    private Store pickupStore;

    /**
     * 还车门店
     * 多对一关联 stores 表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_store_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_return_store"))
    private Store returnStore;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "actual_return_time")
    private LocalDateTime actualReturnTime;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 订单状态
     * 0: 预订
     * 1: 使用中
     * 2: 已还车
     * 3: 已取消
     */
    @Column(name = "status")
    private Integer status = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
