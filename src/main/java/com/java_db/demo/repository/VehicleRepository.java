package com.java_db.demo.repository;

import com.java_db.demo.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 车辆数据访问层
 * 继承 JpaRepository 自动获得 CRUD 操作
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    
    /**
     * 根据门店 ID 和车辆状态查询车辆列表
     * 用于查询某门店的可租车辆
     * Spring Data JPA 自动解析方法名生成查询：
     * SELECT * FROM vehicles WHERE store_id = ? AND status = ?
     * 
     * @param storeId 门店 ID
     * @param status 车辆状态 (0:空闲, 1:已租, 2:维修, 3:调拨中)
     * @return 符合条件的车辆列表
     */
    List<Vehicle> findByStoreIdAndStatus(Integer storeId, Integer status);
    
    /**
     * 根据车辆分类查询车辆列表
     * 
     * @param categoryId 分类 ID
     * @return 该分类下的所有车辆
     */
    List<Vehicle> findByCategoryId(Integer categoryId);
    
    /**
     * 根据车牌号查询车辆
     * 
     * @param plateNumber 车牌号
     * @return 车辆信息
     */
    Vehicle findByPlateNumber(String plateNumber);
    
    /**
     * 查询特定时间段内可用的车辆（高级查询）
     * 排除在指定时间段内有未完成订单的车辆
     * 
     * 查询逻辑：
     * 1. 车辆状态为空闲 (status = 0)
     * 2. 车辆不在指定时间段内有进行中的订单 (status IN (0,1))
     * 3. 订单时间判断：NOT (订单开始时间 < 查询结束时间 AND 订单结束时间 > 查询开始时间)
     * 
     * @param storeId 门店 ID
     * @param startTime 租赁开始时间
     * @param endTime 租赁结束时间
     * @return 可用车辆列表
     */
    @Query("SELECT v FROM Vehicle v WHERE v.store.id = :storeId " +
           "AND v.status = 0 " +
           "AND v.id NOT IN (" +
           "  SELECT o.vehicle.id FROM Order o " +
           "  WHERE o.status IN (0, 1) " +
           "  AND o.startTime < :endTime " +
           "  AND o.endTime > :startTime" +
           ")")
    List<Vehicle> findAvailableVehicles(
        @Param("storeId") Integer storeId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 根据门店查询所有车辆
     * 
     * @param storeId 门店 ID
     * @return 该门店的所有车辆
     */
    List<Vehicle> findByStoreId(Integer storeId);
    
    // ==================== 报表统计查询方法 ====================
    
    /**
     * 按状态统计车辆数量
     */
    @Query("SELECT v.status as status, COUNT(v) as count FROM Vehicle v GROUP BY v.status")
    List<Object[]> countVehiclesByStatus();
    
    /**
     * 按分类统计车辆数量
     */
    @Query("SELECT v.category.id as categoryId, v.category.name as categoryName, COUNT(v) as count " +
           "FROM Vehicle v GROUP BY v.category.id, v.category.name")
    List<Object[]> countVehiclesByCategory();
    
    /**
     * 按门店统计车辆数量
     */
    @Query("SELECT v.store.id as storeId, v.store.name as storeName, COUNT(v) as count " +
           "FROM Vehicle v GROUP BY v.store.id, v.store.name")
    List<Object[]> countVehiclesByStore();
}

