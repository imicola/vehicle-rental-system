package com.java_db.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 门店收入统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreRevenueDTO {
    private Long storeId;
    private String storeName;
    private String address;
    private Integer vehicleCount;          // 车辆数量
    private Integer orderCount;            // 订单数量
    private Double totalRevenue;           // 总收入
    private Double maintenanceCost;        // 维修成本
    private Double netProfit;              // 净利润
    private Double averageUtilization;     // 平均利用率
}
