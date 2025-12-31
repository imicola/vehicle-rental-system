package com.java_db.demo.repository;

import com.java_db.demo.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 维修记录数据访问层
 * 继承 JpaRepository 自动获得 CRUD 操作
 */
@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {
    
    /**
     * 根据车辆 ID 查询所有维修记录
     * Spring Data JPA 自动解析方法名生成查询
     * 
     * @param vehicleId 车辆 ID
     * @return 维修记录列表
     */
    List<Maintenance> findByVehicleId(Integer vehicleId);
    
    // ==================== 报表统计查询方法 ====================
    
    /**
     * 按车辆ID统计维修次数和总成本
     */
    @Query("SELECT m.vehicle.id as vehicleId, COUNT(m) as maintenanceCount, " +
           "COALESCE(SUM(m.cost), 0) as totalCost FROM Maintenance m " +
           "GROUP BY m.vehicle.id")
    List<Object[]> getVehicleMaintenanceStatistics();
    
    /**
     * 按车辆ID和维修类型统计次数
     */
    @Query("SELECT m.vehicle.id as vehicleId, m.type as type, COUNT(m) as count FROM Maintenance m " +
           "GROUP BY m.vehicle.id, m.type")
    List<Object[]> getVehicleMaintenanceCountByType();
    
    /**
     * 统计指定时间范围内的维修总成本
     */
    @Query("SELECT COALESCE(SUM(m.cost), 0) FROM Maintenance m " +
           "WHERE m.startDate BETWEEN :startDate AND :endDate")
    Double sumTotalCostBetweenDates(@Param("startDate") LocalDate startDate, 
                                    @Param("endDate") LocalDate endDate);
    
    /**
     * 按维修类型统计成本
     */
    @Query("SELECT m.type as type, COALESCE(SUM(m.cost), 0) as totalCost FROM Maintenance m " +
           "WHERE m.startDate BETWEEN :startDate AND :endDate " +
           "GROUP BY m.type")
    List<Object[]> sumCostByTypeBetweenDates(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
}

