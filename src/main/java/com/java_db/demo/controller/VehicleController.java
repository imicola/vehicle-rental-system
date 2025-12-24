package com.java_db.demo.controller;

import com.java_db.demo.dto.VehicleDTO;
import com.java_db.demo.entity.Vehicle;
import com.java_db.demo.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 车辆控制器
 * 处理车辆查询、添加等请求
 */
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "车辆接口", description = "车辆查询、管理等相关接口")
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    /**
     * 搜索可用车辆（用户端核心功能）
     * 
     * @param storeId 门店 ID
     * @param start 租赁开始时间
     * @param end 预计还车时间
     * @return 可用车辆列表
     */
    @GetMapping
    @Operation(summary = "搜索可用车辆", description = "查询指定门店在特定时间段内可租赁的车辆")
    public ResponseEntity<List<Vehicle>> searchAvailableVehicles(
            @Parameter(description = "门店ID") @RequestParam Integer storeId,
            @Parameter(description = "租赁开始时间", example = "2025-01-01T10:00:00") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "预计还车时间", example = "2025-01-03T10:00:00") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        List<Vehicle> vehicles = vehicleService.searchAvailableVehicles(storeId, start, end);
        return ResponseEntity.ok(vehicles);
    }
    
    /**
     * 查询车辆详情
     * 
     * @param id 车辆 ID
     * @return 车辆详细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询车辆详情", description = "根据车辆 ID 查询详细信息")
    public ResponseEntity<Vehicle> getVehicleById(
            @Parameter(description = "车辆ID") @PathVariable Integer id) {
        Vehicle vehicle = vehicleService.findById(id);
        return ResponseEntity.ok(vehicle);
    }
    
    /**
     * 添加车辆（管理员功能）
     * 
     * @param vehicleDTO 车辆信息
     * @return 新增的车辆
     */
    @PostMapping
    @Operation(summary = "添加车辆", description = "管理员添加新车辆到系统")
    public ResponseEntity<Vehicle> addVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleService.addVehicle(vehicleDTO);
        return ResponseEntity.ok(vehicle);
    }
    
    /**
     * 更新车辆状态（管理员功能）
     * 
     * @param id 车辆 ID
     * @param status 新状态 (0:空闲, 1:已租, 2:维修, 3:调拨)
     * @return 成功消息
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新车辆状态", description = "管理员更新车辆状态（空闲/已租/维修/调拨）")
    public ResponseEntity<String> updateVehicleStatus(
            @Parameter(description = "车辆ID") @PathVariable Integer id,
            @Parameter(description = "新状态 (0:空闲, 1:已租, 2:维修, 3:调拨)") @RequestParam Integer status) {
        vehicleService.updateVehicleStatus(id, status);
        return ResponseEntity.ok("车辆状态更新成功");
    }
    
    /**
     * 查询门店的所有车辆（管理员功能）
     * 
     * @param storeId 门店 ID
     * @return 该门店的所有车辆
     */
    @GetMapping("/store/{storeId}")
    @Operation(summary = "查询门店车辆", description = "查询指定门店的所有车辆")
    public ResponseEntity<List<Vehicle>> getVehiclesByStore(
            @Parameter(description = "门店ID") @PathVariable Integer storeId) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByStore(storeId);
        return ResponseEntity.ok(vehicles);
    }
    
    /**
     * 查询所有车辆（管理员功能）
     * 
     * @return 所有车辆列表
     */
    @GetMapping("/all")
    @Operation(summary = "查询所有车辆", description = "管理员查询系统中所有车辆")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }
}
