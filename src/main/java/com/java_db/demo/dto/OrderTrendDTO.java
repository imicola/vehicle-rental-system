package com.java_db.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单趋势统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTrendDTO {
    private String period;                 // 时间段
    private Integer totalOrders;           // 总订单数
    private Integer pendingOrders;         // 预订中
    private Integer activeOrders;          // 使用中
    private Integer completedOrders;       // 已完成
    private Integer cancelledOrders;       // 已取消
    private Double totalAmount;            // 总金额
    private Double completionRate;         // 完成率（%）
    private Double cancellationRate;       // 取消率（%）
}
