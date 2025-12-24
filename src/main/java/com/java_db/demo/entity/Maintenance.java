package com.java_db.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 维修记录实体类
 * 对应数据库表: maintenance
 * 
 * 类型说明: 维修、保养、年检等
 */
@Entity
@Table(name = "maintenance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 关联车辆
     * 多对一关联 vehicles 表
     * 删除车辆时级联删除维修记录 (ON DELETE CASCADE)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, foreignKey = @ForeignKey(name = "fk_maintenance_vehicle"))
    private Vehicle vehicle;

    /**
     * 维修类型
     * 如: 维修、保养、年检
     */
    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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
