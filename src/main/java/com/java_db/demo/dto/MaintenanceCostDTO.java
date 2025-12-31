package com.java_db.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 维修成本统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceCostDTO {
    private Long vehicleId;
    private String licensePlate;
    private String model;
    private String categoryName;
    private Integer maintenanceCount;      // 维修次数
    private Integer repairCount;           // 维修类型次数
    private Integer serviceCount;          // 保养类型次数
    private Integer inspectionCount;       // 年检类型次数
    private Double totalCost;              // 总维修成本
    private Double averageCost;            // 平均维修成本
    private Double revenueMinusCost;       // 净收入（收入-维修成本）
}
