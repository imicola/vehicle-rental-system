package com.java_db.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 车辆利用率DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUtilizationDTO {
    private Long vehicleId;
    private String licensePlate;
    private String model;
    private String categoryName;
    private String storeName;
    private Integer totalOrders;           // 总订单数
    private Long totalRentalDays;          // 总租赁天数
    private Double utilizationRate;        // 利用率（%）
    private Double totalRevenue;           // 总收入
    private Integer status;                // 当前状态
}
