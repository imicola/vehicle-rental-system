package com.java_db.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车辆实体类
 * 对应数据库表: vehicles
 * 
 * 状态说明:
 * 0 - 空闲
 * 1 - 已租
 * 2 - 维修中
 * 3 - 调拨中
 */
@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "plate_number", nullable = false, unique = true, length = 20)
    private String plateNumber;

    @Column(name = "model", length = 50)
    private String model;

    /**
     * 所属分类
     * 多对一关联 categories 表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vehicle_category"))
    private Category category;

    /**
     * 所属门店
     * 多对一关联 stores 表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vehicle_store"))
    private Store store;

    /**
     * 车辆状态
     * 0: 空闲
     * 1: 已租
     * 2: 维修中
     * 3: 调拨中
     */
    @Column(name = "status")
    private Integer status = 0;

    @Column(name = "daily_rate", precision = 10, scale = 2)
    private BigDecimal dailyRate;

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
