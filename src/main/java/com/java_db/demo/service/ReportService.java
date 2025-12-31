package com.java_db.demo.service;

import com.java_db.demo.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表服务接口
 */
public interface ReportService {
    
    /**
     * 获取综合仪表盘数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 仪表盘数据
     */
    DashboardDTO getDashboard(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取收入统计报表
     * 
     * @param period 时间周期（DAY/WEEK/MONTH/YEAR）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收入统计列表
     */
    List<RevenueStatisticsDTO> getRevenueStatistics(ReportPeriod period, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取车辆利用率报表
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 车辆利用率列表
     */
    List<VehicleUtilizationDTO> getVehicleUtilization(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取维修成本分析报表
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 维修成本统计列表
     */
    List<MaintenanceCostDTO> getMaintenanceCost(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取订单趋势报表
     * 
     * @param period 时间周期（DAY/WEEK/MONTH/YEAR）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单趋势列表
     */
    List<OrderTrendDTO> getOrderTrend(ReportPeriod period, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取门店收入统计报表
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 门店收入统计列表
     */
    List<StoreRevenueDTO> getStoreRevenue(LocalDateTime startDate, LocalDateTime endDate);
}
