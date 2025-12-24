package com.java_db.demo.repository;

import com.java_db.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问层
 * 继承 JpaRepository 自动获得 CRUD 操作
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    /**
     * 根据用户 ID 查询所有订单
     * 用于用户查看自己的订单历史
     * Spring Data JPA 自动解析方法名生成查询：
     * SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC
     * 
     * @param userId 用户 ID
     * @return 该用户的所有订单列表（按创建时间倒序）
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    /**
     * 根据用户 ID 查询所有订单（简化版）
     * 
     * @param userId 用户 ID
     * @return 该用户的所有订单列表
     */
    List<Order> findByUserId(Integer userId);
    
    /**
     * 根据车辆 ID 和订单状态列表查询订单
     * 用于检查某车辆是否有未完成的订单（预订、使用中）
     * Spring Data JPA 自动解析方法名生成查询：
     * SELECT * FROM orders WHERE vehicle_id = ? AND status IN (?)
     * 
     * @param vehicleId 车辆 ID
     * @param statuses 订单状态列表 (0:预订, 1:使用中, 2:已还车, 3:已取消)
     * @return 符合条件的订单列表
     */
    List<Order> findByVehicleIdAndStatusIn(Integer vehicleId, List<Integer> statuses);
    
    /**
     * 根据订单流水号查询订单
     * 
     * @param orderNo 订单流水号
     * @return 订单信息
     */
    Optional<Order> findByOrderNo(String orderNo);
    
    /**
     * 根据订单状态查询订单
     * 
     * @param status 订单状态
     * @return 该状态下的所有订单
     */
    List<Order> findByStatus(Integer status);
    
    /**
     * 查询特定时间段内某车辆的冲突订单
     * 用于检查车辆在指定时间段是否可租
     * 
     * 时间冲突判断逻辑：
     * 新订单 [startTime, endTime] 与现有订单 [order.startTime, order.endTime] 冲突的条件：
     * order.startTime < endTime AND order.endTime > startTime
     * 
     * @param vehicleId 车辆 ID
     * @param startTime 租赁开始时间
     * @param endTime 租赁结束时间
     * @param statuses 需要检查的订单状态（通常是预订和使用中）
     * @return 冲突的订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.vehicle.id = :vehicleId " +
           "AND o.status IN :statuses " +
           "AND o.startTime < :endTime " +
           "AND o.endTime > :startTime")
    List<Order> findConflictingOrders(
        @Param("vehicleId") Integer vehicleId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("statuses") List<Integer> statuses
    );
    
    /**
     * 根据取车门店查询订单
     * 
     * @param pickupStoreId 取车门店 ID
     * @return 该门店的所有取车订单
     */
    List<Order> findByPickupStoreId(Integer pickupStoreId);
    
    /**
     * 根据还车门店查询订单
     * 
     * @param returnStoreId 还车门店 ID
     * @return 该门店的所有还车订单
     */
    List<Order> findByReturnStoreId(Integer returnStoreId);
}
