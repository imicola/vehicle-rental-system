package com.java_db.demo.service;

import com.java_db.demo.dto.OrderDTO;
import com.java_db.demo.entity.Order;
import com.java_db.demo.entity.Store;
import com.java_db.demo.entity.User;
import com.java_db.demo.entity.Vehicle;
import com.java_db.demo.exception.BusinessException;
import com.java_db.demo.exception.ResourceNotFoundException;
import com.java_db.demo.repository.OrderRepository;
import com.java_db.demo.repository.StoreRepository;
import com.java_db.demo.repository.UserRepository;
import com.java_db.demo.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 订单管理服务
 * 处理订单创建、还车、取消等核心业务逻辑
 * 
 * 核心难点：
 * 1. 时间冲突检验（避免同一车辆在同一时间段被多次预订）
 * 2. 并发控制（使用事务隔离级别保证数据一致性）
 * 3. 异地还车逻辑（更新车辆所属门店）
 * 4. 逾期罚金计算
 */
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    
    // 超期费率：日租金的 1.5 倍
    private static final BigDecimal OVERDUE_RATE = new BigDecimal("1.5");
    
    /**
     * 创建订单（核心功能）
     * 
     * 逻辑：
     * 1. 检查时间冲突（避免同一车辆在同一时间段被多次预订）
     * 2. 计算订单总金额 = 日租金 × 天数
     * 3. 生成订单流水号（UUID）
     * 4. 更新车辆状态为"已租"
     * 5. 保存订单
     * 
     * 并发控制：使用 READ_COMMITTED 事务隔离级别
     * 
     * @param orderDTO 订单信息
     * @return 创建的订单
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order createOrder(OrderDTO orderDTO) {
        // 1. 参数验证
        if (orderDTO.getStartTime().isAfter(orderDTO.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        
        if (orderDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }
        
        // 2. 查询关联实体
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        
        Vehicle vehicle = vehicleRepository.findById(orderDTO.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("车辆不存在"));
        
        Store pickupStore = storeRepository.findById(orderDTO.getPickupStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("取车门店不存在"));
        
        Store returnStore = storeRepository.findById(orderDTO.getReturnStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("还车门店不存在"));
        
        // 3. 检查车辆状态
        if (vehicle.getStatus() != 0) {
            throw new BusinessException("车辆当前不可租赁（状态：" + getStatusText(vehicle.getStatus()) + "）");
        }
        
        // 4. 检查时间冲突（核心逻辑）
        List<Order> conflictingOrders = orderRepository.findConflictingOrders(
                vehicle.getId(),
                orderDTO.getStartTime(),
                orderDTO.getEndTime(),
                Arrays.asList(0, 1) // 预订和使用中的订单
        );
        
        if (!conflictingOrders.isEmpty()) {
            throw new BusinessException("该车辆在指定时间段已被预订");
        }
        
        // 5. 计算订单金额
        long days = Duration.between(orderDTO.getStartTime(), orderDTO.getEndTime()).toDays();
        if (days == 0) {
            days = 1; // 至少按1天计算
        }
        BigDecimal totalAmount = vehicle.getDailyRate().multiply(BigDecimal.valueOf(days));
        
        // 6. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUser(user);
        order.setVehicle(vehicle);
        order.setPickupStore(pickupStore);
        order.setReturnStore(returnStore);
        order.setStartTime(orderDTO.getStartTime());
        order.setEndTime(orderDTO.getEndTime());
        order.setTotalAmount(totalAmount);
        order.setStatus(0); // 预订状态
        
        // 7. 更新车辆状态为"已租"
        vehicle.setStatus(1);
        vehicleRepository.save(vehicle);
        
        // 8. 保存订单
        return orderRepository.save(order);
    }
    
    /**
     * 还车（核心功能）
     * 
     * 逻辑：
     * 1. 更新实际还车时间
     * 2. 计算是否逾期及罚金
     * 3. 更新订单状态为"已还车"
     * 4. 异地还车：更新车辆所属门店
     * 5. 恢复车辆状态为"空闲"
     * 
     * @param orderId 订单 ID
     * @param returnStoreId 实际还车门店 ID
     * @return 更新后的订单（包含罚金信息）
     */
    @Transactional
    public Order returnVehicle(Integer orderId, Integer returnStoreId) {
        // 1. 查询订单
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        
        // 2. 检查订单状态
        if (order.getStatus() == 2) {
            throw new BusinessException("订单已完成，无法重复还车");
        }
        if (order.getStatus() == 3) {
            throw new BusinessException("订单已取消");
        }
        
        // 3. 查询还车门店
        Store returnStore = storeRepository.findById(returnStoreId)
                .orElseThrow(() -> new ResourceNotFoundException("还车门店不存在"));
        
        // 4. 更新实际还车时间
        LocalDateTime actualReturnTime = LocalDateTime.now();
        order.setActualReturnTime(actualReturnTime);
        
        // 5. 计算逾期罚金（如果逾期）
        BigDecimal penalty = BigDecimal.ZERO;
        if (actualReturnTime.isAfter(order.getEndTime())) {
            long overdueDays = Duration.between(order.getEndTime(), actualReturnTime).toDays();
            if (overdueDays == 0) {
                overdueDays = 1; // 至少按1天计算
            }
            // 罚金 = 日租金 × 超期天数 × 超期费率（1.5倍）
            penalty = order.getVehicle().getDailyRate()
                    .multiply(BigDecimal.valueOf(overdueDays))
                    .multiply(OVERDUE_RATE);
            
            // 更新订单总金额（加上罚金）
            order.setTotalAmount(order.getTotalAmount().add(penalty));
        }
        
        // 6. 更新订单状态为"已还车"
        order.setStatus(2);
        
        // 7. 异地还车逻辑：更新车辆所属门店
        Vehicle vehicle = order.getVehicle();
        if (!vehicle.getStore().getId().equals(returnStoreId)) {
            vehicle.setStore(returnStore);
        }
        
        // 8. 恢复车辆状态为"空闲"
        vehicle.setStatus(0);
        vehicleRepository.save(vehicle);
        
        // 9. 保存订单
        return orderRepository.save(order);
    }
    
    /**
     * 取消订单
     * 
     * @param orderId 订单 ID
     */
    @Transactional
    public void cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        
        // 检查订单状态
        if (order.getStatus() == 2) {
            throw new BusinessException("订单已完成，无法取消");
        }
        if (order.getStatus() == 3) {
            throw new BusinessException("订单已取消");
        }
        
        // 更新订单状态
        order.setStatus(3);
        
        // 恢复车辆状态为空闲
        Vehicle vehicle = order.getVehicle();
        vehicle.setStatus(0);
        vehicleRepository.save(vehicle);
        
        orderRepository.save(order);
    }
    
    /**
     * 查询用户的所有订单
     * 
     * @param userId 用户 ID
     * @return 订单列表
     */
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Integer userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * 查询所有订单（管理员功能）
     * 
     * @return 所有订单列表
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    /**
     * 根据订单流水号查询订单
     * 
     * @param orderNo 订单流水号
     * @return 订单信息
     */
    @Transactional(readOnly = true)
    public Order findByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
    }
    
    /**
     * 根据 ID 查询订单
     * 
     * @param orderId 订单 ID
     * @return 订单信息
     */
    @Transactional(readOnly = true)
    public Order findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
    }
    
    /**
     * 生成订单流水号
     * 使用 UUID 保证唯一性
     * 
     * @return 订单流水号
     */
    private String generateOrderNo() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * 获取车辆状态文本
     * 
     * @param status 状态码
     * @return 状态文本
     */
    private String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "空闲";
            case 1 -> "已租";
            case 2 -> "维修中";
            case 3 -> "调拨中";
            default -> "未知";
        };
    }
}
