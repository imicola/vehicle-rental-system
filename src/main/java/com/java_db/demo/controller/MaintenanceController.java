package com.java_db.demo.controller;

import com.java_db.demo.entity.Maintenance;
import com.java_db.demo.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 维修控制器
 * 处理车辆维修记录查询、管理等请求
 */
@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@Tag(name = "维修接口", description = "车辆维修记录查询、管理等相关接口")
public class MaintenanceController {
    
    private final MaintenanceService maintenanceService;
    
    /**
     * 创建维修记录（管理员功能）
     * 
     * @param vehicleId 车辆 ID
     * @param type 维修类型（维修/保养/年检）
     * @param startDate 开始日期
     * @param cost 费用
     * @param description 描述
     * @return 维修记录
     */
    @PostMapping
    @Operation(summary = "创建维修记录", description = "管理员创建维修记录，车辆状态将自动更新为维修中")
    public ResponseEntity<Maintenance> createMaintenanceRecord(
            @Parameter(description = "车辆ID") @RequestParam Integer vehicleId,
            @Parameter(description = "维修类型（维修/保养/年检）") @RequestParam String type,
            @Parameter(description = "开始日期") @RequestParam LocalDate startDate,
            @Parameter(description = "费用") @RequestParam BigDecimal cost,
            @Parameter(description = "描述") @RequestParam(required = false) String description) {
        
        Maintenance maintenance = maintenanceService.createMaintenanceRecord(
                vehicleId, type, startDate, cost, description);
        return ResponseEntity.ok(maintenance);
    }
    
    /**
     * 完成维修记录（管理员功能）
     * 
     * @param id 维修记录 ID
     * @return 更新后的维修记录
     */
    @PutMapping("/{id}/complete")
    @Operation(summary = "完成维修", description = "标记维修完成，车辆状态将恢复为空闲")
    public ResponseEntity<Maintenance> completeMaintenanceRecord(
            @Parameter(description = "维修记录ID") @PathVariable Integer id) {
        Maintenance maintenance = maintenanceService.completeMaintenanceRecord(id);
        return ResponseEntity.ok(maintenance);
    }
    
    /**
     * 查询车辆的维修记录
     * 
     * @param vehicleId 车辆 ID
     * @return 维修记录列表
     */
    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "查询车辆维修记录", description = "查询指定车辆的所有维修历史")
    public ResponseEntity<List<Maintenance>> getMaintenanceByVehicle(
            @Parameter(description = "车辆ID") @PathVariable Integer vehicleId) {
        List<Maintenance> maintenances = maintenanceService.getMaintenanceByVehicle(vehicleId);
        return ResponseEntity.ok(maintenances);
    }
    
    /**
     * 查询所有维修记录（管理员功能）
     * 
     * @return 所有维修记录
     */
    @GetMapping("/all")
    @Operation(summary = "查询所有维修记录", description = "管理员查询系统中所有维修记录")
    public ResponseEntity<List<Maintenance>> getAllMaintenances() {
        List<Maintenance> maintenances = maintenanceService.getAllMaintenances();
        return ResponseEntity.ok(maintenances);
    }
}
