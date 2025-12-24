package com.java_db.demo.repository;

import com.java_db.demo.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
