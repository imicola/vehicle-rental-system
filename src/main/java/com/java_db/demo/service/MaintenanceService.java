package com.java_db.demo.service;

import com.java_db.demo.entity.Maintenance;
import com.java_db.demo.entity.Vehicle;
import com.java_db.demo.exception.ResourceNotFoundException;
import com.java_db.demo.repository.MaintenanceRepository;
import com.java_db.demo.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 维修管理服务
 * 处理车辆维修记录和状态管理
 * 
 * 集成逻辑：
 * - 创建维修记录时，自动更新车辆状态为"维修中"(2)
 * - 完成维修时，恢复车辆状态为"空闲"(0)
 */
@Service
@RequiredArgsConstructor
public class MaintenanceService {
    
    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    
    /**
     * 创建维修记录
     * 自动更新车辆状态为"维修中"
     * 
     * @param vehicleId 车辆 ID
     * @param type 维修类型（维修、保养、年检）
     * @param startDate 开始日期
     * @param cost 费用
     * @param description 描述
     * @return 维修记录
     */
    @Transactional
    public Maintenance createMaintenanceRecord(
            Integer vehicleId,
            String type,
            LocalDate startDate,
            BigDecimal cost,
            String description) {
        
        // 查询车辆
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("车辆不存在"));
        
        // 更新车辆状态为"维修中"
        vehicle.setStatus(2);
        vehicleRepository.save(vehicle);
        
        // 创建维修记录
        Maintenance maintenance = new Maintenance();
        maintenance.setVehicle(vehicle);
        maintenance.setType(type);
        maintenance.setStartDate(startDate);
        maintenance.setCost(cost);
        maintenance.setDescription(description);
        
        return maintenanceRepository.save(maintenance);
    }
    
    /**
     * 完成维修记录
     * 恢复车辆状态为"空闲"
     * 
     * @param maintenanceId 维修记录 ID
     */
    @Transactional
    public Maintenance completeMaintenanceRecord(Integer maintenanceId) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("维修记录不存在"));
        
        // 设置完成日期
        maintenance.setEndDate(LocalDate.now());
        
        // 恢复车辆状态为"空闲"
        Vehicle vehicle = maintenance.getVehicle();
        vehicle.setStatus(0);
        vehicleRepository.save(vehicle);
        
        return maintenanceRepository.save(maintenance);
    }
    
    /**
     * 查询车辆的所有维修记录
     * 
     * @param vehicleId 车辆 ID
     * @return 维修记录列表
     */
    @Transactional(readOnly = true)
    public List<Maintenance> getMaintenanceByVehicle(Integer vehicleId) {
        return maintenanceRepository.findByVehicleId(vehicleId);
    }
    
    /**
     * 查询所有维修记录（管理员功能）
     * 
     * @return 所有维修记录
     */
    @Transactional(readOnly = true)
    public List<Maintenance> getAllMaintenances() {
        return maintenanceRepository.findAll();
    }
    
    /**
     * 根据 ID 查询维修记录
     * 
     * @param maintenanceId 维修记录 ID
     * @return 维修记录
     */
    @Transactional(readOnly = true)
    public Maintenance findById(Integer maintenanceId) {
        return maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("维修记录不存在"));
    }
}
