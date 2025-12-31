package com.java_db.demo.controller;

import com.java_db.demo.dto.*;
import com.java_db.demo.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表控制器
 * 提供数据分析和报表相关的API接口（仅限管理员访问）
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "报表分析接口", description = "数据分析、统计报表等管理员功能")
// @PreAuthorize("hasRole('ADMIN')")  // 限制仅管理员可访问 - 需要Spring Security配置
public class ReportController {
    
    private final ReportService reportService;
    
    /**
     * 获取综合仪表盘数据
     * 
     * @param startDate 开始日期时间
     * @param endDate 结束日期时间
     * @return 仪表盘综合数据
     */
    @GetMapping("/dashboard")
    @Operation(summary = "获取综合仪表盘", description = "获取指定时间范围内的综合运营数据，包括收入、订单、车辆等关键指标")
    public ResponseEntity<DashboardDTO> getDashboard(
            @Parameter(description = "开始日期时间", example = "2025-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束日期时间", example = "2025-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        DashboardDTO dashboard = reportService.getDashboard(startDate, endDate);
        return ResponseEntity.ok(dashboard);
    }
    
    /**
     * 获取收入统计报表
     * 
     * @param period 统计周期（DAY/WEEK/MONTH/YEAR）
     * @param startDate 开始日期时间
     * @param endDate 结束日期时间
     * @return 收入统计列表
     */
    @GetMapping("/revenue")
    @Operation(summary = "收入统计报表", description = "按时间周期统计收入情况，包括押金、尾款、罚金等明细")
    public ResponseEntity<List<RevenueStatisticsDTO>> getRevenueStatistics(
            @Parameter(description = "统计周期", example = "MONTH")
            @RequestParam ReportPeriod period,
            @Parameter(description = "开始日期时间", example = "2025-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束日期时间", example = "2025-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<RevenueStatisticsDTO> statistics = reportService.getRevenueStatistics(period, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 获取车辆利用率报表
     * 
     * @param startDate 开始日期时间
     * @param endDate 结束日期时间
     * @return 车辆利用率列表
     */
    @GetMapping("/vehicle-utilization")
    @Operation(summary = "车辆利用率报表", description = "统计各车辆的租赁次数、租赁天数、利用率和收入情况")
    public ResponseEntity<List<VehicleUtilizationDTO>> getVehicleUtilization(
            @Parameter(description = "开始日期时间", example = "2025-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束日期时间", example = "2025-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<VehicleUtilizationDTO> utilization = reportService.getVehicleUtilization(startDate, endDate);
        return ResponseEntity.ok(utilization);
    }
    
    /**
     * 获取维修成本分析报表
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 维修成本统计列表
     */
    @GetMapping("/maintenance-cost")
    @Operation(summary = "维修成本分析", description = "统计各车辆的维修次数、维修成本和净利润（收入-成本）")
    public ResponseEntity<List<MaintenanceCostDTO>> getMaintenanceCost(
            @Parameter(description = "开始日期", example = "2025-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<MaintenanceCostDTO> costs = reportService.getMaintenanceCost(startDate, endDate);
        return ResponseEntity.ok(costs);
    }
    
    /**
     * 获取订单趋势报表
     * 
     * @param period 统计周期（DAY/WEEK/MONTH/YEAR）
     * @param startDate 开始日期时间
     * @param endDate 结束日期时间
     * @return 订单趋势列表
     */
    @GetMapping("/order-trend")
    @Operation(summary = "订单趋势分析", description = "按时间周期统计订单数量、状态分布、完成率和取消率")
    public ResponseEntity<List<OrderTrendDTO>> getOrderTrend(
            @Parameter(description = "统计周期", example = "MONTH")
            @RequestParam ReportPeriod period,
            @Parameter(description = "开始日期时间", example = "2025-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束日期时间", example = "2025-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<OrderTrendDTO> trend = reportService.getOrderTrend(period, startDate, endDate);
        return ResponseEntity.ok(trend);
    }
    
    /**
     * 获取门店收入统计报表
     * 
     * @param startDate 开始日期时间
     * @param endDate 结束日期时间
     * @return 门店收入统计列表
     */
    @GetMapping("/store-revenue")
    @Operation(summary = "门店收入统计", description = "统计各门店的车辆数、订单数、收入、成本和净利润")
    public ResponseEntity<List<StoreRevenueDTO>> getStoreRevenue(
            @Parameter(description = "开始日期时间", example = "2025-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束日期时间", example = "2025-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<StoreRevenueDTO> revenue = reportService.getStoreRevenue(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }
}
