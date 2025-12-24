package com.java_db.demo.service;

import com.java_db.demo.dto.VehicleDTO;
import com.java_db.demo.entity.Category;
import com.java_db.demo.entity.Store;
import com.java_db.demo.entity.Vehicle;
import com.java_db.demo.exception.BusinessException;
import com.java_db.demo.exception.ResourceNotFoundException;
import com.java_db.demo.repository.CategoryRepository;
import com.java_db.demo.repository.StoreRepository;
import com.java_db.demo.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 车辆管理服务
 * 处理车辆查询、状态管理等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    
    /**
     * 搜索可用车辆（用户端核心功能）
     * 查询指定门店在特定时间段内可租赁的车辆
     * 
     * 逻辑：
     * 1. 找出所有属于指定门店的车辆
     * 2. 排除 status != 0 (非空闲状态) 的车辆
     * 3. 排除在指定时间段内已有订单的车辆
     * 
     * @param storeId 门店 ID
     * @param startTime 租赁开始时间
     * @param endTime 预计还车时间
     * @return 可用车辆列表
     */
    @Transactional(readOnly = true)
    public List<Vehicle> searchAvailableVehicles(Integer storeId, LocalDateTime startTime, LocalDateTime endTime) {
        // 参数验证
        if (startTime.isAfter(endTime)) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }
        
        // 调用 Repository 的自定义 JPQL 查询
        return vehicleRepository.findAvailableVehicles(storeId, startTime, endTime);
    }
    
    /**
     * 根据门店和状态查询车辆（基础查询）
     * 
     * @param storeId 门店 ID
     * @param status 车辆状态 (0:空闲, 1:已租, 2:维修, 3:调拨)
     * @return 符合条件的车辆列表
     */
    @Transactional(readOnly = true)
    public List<Vehicle> findByStoreAndStatus(Integer storeId, Integer status) {
        return vehicleRepository.findByStoreIdAndStatus(storeId, status);
    }
    
    /**
     * 添加车辆（管理员功能）
     * 
     * @param vehicleDTO 车辆信息
     * @return 新增的车辆
     */
    @Transactional
    public Vehicle addVehicle(VehicleDTO vehicleDTO) {
        // 检查车牌号是否已存在
        if (vehicleRepository.findByPlateNumber(vehicleDTO.getPlateNumber()) != null) {
            throw new BusinessException("车牌号已存在");
        }
        
        // 查询关联的门店和分类
        Store store = storeRepository.findById(vehicleDTO.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("门店不存在"));
        
        Category category = categoryRepository.findById(vehicleDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("分类不存在"));
        
        // 创建车辆
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber(vehicleDTO.getPlateNumber());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setCategory(category);
        vehicle.setStore(store);
        vehicle.setDailyRate(vehicleDTO.getDailyRate());
        vehicle.setStatus(0); // 默认空闲
        
        return vehicleRepository.save(vehicle);
    }
    
    /**
     * 更新车辆状态（管理员功能）
     * 用于手动标记车辆为维修中、调拨中等状态
     * 
     * @param vehicleId 车辆 ID
     * @param newStatus 新状态 (0:空闲, 1:已租, 2:维修, 3:调拨)
     */
    @Transactional
    public void updateVehicleStatus(Integer vehicleId, Integer newStatus) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("车辆不存在"));
        
        // 状态验证
        if (newStatus < 0 || newStatus > 3) {
            throw new BusinessException("无效的车辆状态");
        }
        
        vehicle.setStatus(newStatus);
        vehicleRepository.save(vehicle);
    }
    
    /**
     * 根据门店查询所有车辆
     * 
     * @param storeId 门店 ID
     * @return 该门店的所有车辆
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByStore(Integer storeId) {
        return vehicleRepository.findByStoreId(storeId);
    }
    
    /**
     * 根据分类查询车辆
     * 
     * @param categoryId 分类 ID
     * @return 该分类下的所有车辆
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByCategory(Integer categoryId) {
        return vehicleRepository.findByCategoryId(categoryId);
    }
    
    /**
     * 查询所有车辆（管理员功能）
     * 
     * @return 所有车辆列表
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }
    
    /**
     * 根据 ID 查询车辆
     * 
     * @param vehicleId 车辆 ID
     * @return 车辆信息
     */
    @Transactional(readOnly = true)
    public Vehicle findById(Integer vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("车辆不存在"));
    }
}
