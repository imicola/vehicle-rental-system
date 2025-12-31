package com.java_db.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收入统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatisticsDTO {
    private String period;                 // 时间段（如：2025-01-01）
    private Double totalRevenue;           // 总收入
    private Double depositAmount;          // 押金总额
    private Double finalPaymentAmount;     // 尾款总额
    private Double penaltyAmount;          // 罚金总额
    private Integer orderCount;            // 订单数量
    private Integer completedOrderCount;   // 完成订单数
    private Integer cancelledOrderCount;   // 取消订单数
    private Double averageOrderAmount;     // 平均订单金额
}
