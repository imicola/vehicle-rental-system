package com.java_db.demo.controller;

import com.java_db.demo.dto.OrderDTO;
import com.java_db.demo.entity.Order;
import com.java_db.demo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 * 处理订单创建、查询、还车等请求
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "订单接口", description = "订单创建、查询、还车等相关接口")
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * 创建订单
     * 
     * @param orderDTO 订单信息
     * @return 创建的订单
     */
    @PostMapping
    @Operation(summary = "创建订单", description = "用户创建租车订单，会检查时间冲突并计算金额")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        Order order = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(order);
    }
    
    /**
     * 查询用户的所有订单
     * 
     * @param userId 用户 ID
     * @return 用户的订单列表
     */
    @GetMapping("/my")
    @Operation(summary = "我的订单", description = "查询用户的所有订单历史")
    public ResponseEntity<List<Order>> getMyOrders(
            @Parameter(description = "用户ID") @RequestParam Integer userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * 还车
     * 
     * @param id 订单 ID
     * @param storeId 还车门店 ID
     * @return 更新后的订单（包含罚金信息）
     */
    @PostMapping("/{id}/return")
    @Operation(summary = "还车", description = "用户还车，系统会计算逾期罚金并更新车辆状态")
    public ResponseEntity<Order> returnVehicle(
            @Parameter(description = "订单ID") @PathVariable Integer id,
            @Parameter(description = "还车门店ID") @RequestParam Integer storeId) {
        Order order = orderService.returnVehicle(id, storeId);
        return ResponseEntity.ok(order);
    }
    
    /**
     * 取消订单
     * 
     * @param id 订单 ID
     * @return 成功消息
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "用户取消订单，车辆状态将恢复为空闲")
    public ResponseEntity<String> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Integer id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("订单取消成功");
    }
    
    /**
     * 根据订单流水号查询订单
     * 
     * @param orderNo 订单流水号
     * @return 订单信息
     */
    @GetMapping("/no/{orderNo}")
    @Operation(summary = "根据流水号查询订单", description = "根据订单流水号查询订单详情")
    public ResponseEntity<Order> getOrderByNo(
            @Parameter(description = "订单流水号") @PathVariable String orderNo) {
        Order order = orderService.findByOrderNo(orderNo);
        return ResponseEntity.ok(order);
    }
    
    /**
     * 查询订单详情
     * 
     * @param id 订单 ID
     * @return 订单详细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询订单详情", description = "根据订单 ID 查询详细信息")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "订单ID") @PathVariable Integer id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }
    
    /**
     * 查询所有订单（管理员功能）
     * 
     * @return 所有订单列表
     */
    @GetMapping("/all")
    @Operation(summary = "查询所有订单", description = "管理员查询系统中所有订单")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
