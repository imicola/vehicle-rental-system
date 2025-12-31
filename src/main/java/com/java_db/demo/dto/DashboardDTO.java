package com.java_db.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 综合仪表盘DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    // 关键指标
    private Double totalRevenue;              // 总收入
    private Double totalMaintenanceCost;      // 总维修成本
    private Double netProfit;                 // 净利润
    private Integer totalOrders;              // 总订单数
    private Integer completedOrders;          // 完成订单数
    private Double averageUtilizationRate;    // 平均车辆利用率
    
    // 车辆统计
    private Integer totalVehicles;            // 总车辆数
    private Integer availableVehicles;        // 可用车辆数
    private Integer rentedVehicles;           // 已租车辆数
    private Integer maintenanceVehicles;      // 维修中车辆数
    private Integer transferVehicles;         // 调拨中车辆数
    
    // 分布统计
    private Map<String, Integer> vehicleByCategory;    // 按分类统计车辆
    private Map<String, Integer> vehicleByStore;       // 按门店统计车辆
    private Map<String, Integer> orderByStatus;        // 按状态统计订单
    private Map<String, Double> revenueByStore;        // 按门店统计收入
    
    // 趋势数据（最近7天）
    private Double revenueGrowthRate;         // 收入增长率
    private Double orderGrowthRate;           // 订单增长率
}
