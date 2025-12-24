package com.java_db.demo.controller;

import com.java_db.demo.entity.Payment;
import com.java_db.demo.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付控制器
 * 处理支付记录查询、创建等请求
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "支付接口", description = "支付记录查询、创建等相关接口")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * 创建押金支付记录
     * 
     * @param orderId 订单 ID
     * @return 押金支付记录
     */
    @PostMapping("/deposit")
    @Operation(summary = "创建押金支付", description = "为订单创建押金支付记录（日租金×3）")
    public ResponseEntity<Payment> createDepositPayment(
            @Parameter(description = "订单ID") @RequestParam Integer orderId) {
        Payment payment = paymentService.createDepositPayment(orderId);
        return ResponseEntity.ok(payment);
    }
    
    /**
     * 创建尾款支付记录
     * 
     * @param orderId 订单 ID
     * @param amount 尾款金额
     * @return 尾款支付记录
     */
    @PostMapping("/final")
    @Operation(summary = "创建尾款支付", description = "为订单创建尾款支付记录")
    public ResponseEntity<Payment> createFinalPayment(
            @Parameter(description = "订单ID") @RequestParam Integer orderId,
            @Parameter(description = "尾款金额") @RequestParam BigDecimal amount) {
        Payment payment = paymentService.createFinalPayment(orderId, amount);
        return ResponseEntity.ok(payment);
    }
    
    /**
     * 创建罚金支付记录
     * 
     * @param orderId 订单 ID
     * @param amount 罚金金额
     * @return 罚金支付记录
     */
    @PostMapping("/penalty")
    @Operation(summary = "创建罚金支付", description = "为订单创建罚金支付记录（逾期或车损）")
    public ResponseEntity<Payment> createPenaltyPayment(
            @Parameter(description = "订单ID") @RequestParam Integer orderId,
            @Parameter(description = "罚金金额") @RequestParam BigDecimal amount) {
        Payment payment = paymentService.createPenaltyPayment(orderId, amount);
        return ResponseEntity.ok(payment);
    }
    
    /**
     * 查询订单的所有支付记录
     * 
     * @param orderId 订单 ID
     * @return 支付记录列表
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "查询订单支付记录", description = "查询指定订单的所有支付记录")
    public ResponseEntity<List<Payment>> getPaymentsByOrder(
            @Parameter(description = "订单ID") @PathVariable Integer orderId) {
        List<Payment> payments = paymentService.getPaymentsByOrder(orderId);
        return ResponseEntity.ok(payments);
    }
    
    /**
     * 查询所有支付记录（管理员功能）
     * 
     * @return 所有支付记录
     */
    @GetMapping("/all")
    @Operation(summary = "查询所有支付记录", description = "管理员查询系统中所有支付记录")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
}
