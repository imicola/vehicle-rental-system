package com.java_db.demo.service;

import com.java_db.demo.entity.Order;
import com.java_db.demo.entity.Payment;
import com.java_db.demo.exception.ResourceNotFoundException;
import com.java_db.demo.repository.OrderRepository;
import com.java_db.demo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付服务
 * 处理押金、尾款、罚金等支付相关业务逻辑
 * 
 * 支付三阶段流程：
 * 1. 创建订单时 -> 生成押金支付记录（payType='Deposit'）
 * 2. 还车时 -> 生成尾款支付记录（payType='Final'）
 * 3. 逾期/损伤时 -> 生成罚金支付记录（payType='Penalty'）
 */
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    
    // 押金倍数：日租金的 3 倍
    private static final BigDecimal DEPOSIT_RATE = new BigDecimal("3.0");
    
    /**
     * 创建押金支付记录
     * 订单创建时调用，押金 = 日租金 × 3
     * 
     * @param orderId 订单 ID
     * @return 押金支付记录
     */
    @Transactional
    public Payment createDepositPayment(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        
        // 计算押金金额：日租金 × 3
        BigDecimal depositAmount = order.getVehicle().getDailyRate().multiply(DEPOSIT_RATE);
        
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(depositAmount);
        payment.setPayType("Deposit"); // 押金
        payment.setPayTime(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    /**
     * 创建尾款支付记录
     * 还车时调用
     * 
     * @param orderId 订单 ID
     * @param amount 尾款金额
     * @return 尾款支付记录
     */
    @Transactional
    public Payment createFinalPayment(Integer orderId, BigDecimal amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPayType("Final"); // 尾款
        payment.setPayTime(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    /**
     * 创建罚金支付记录
     * 逾期或车辆损伤时调用
     * 
     * @param orderId 订单 ID
     * @param penaltyAmount 罚金金额
     * @return 罚金支付记录
     */
    @Transactional
    public Payment createPenaltyPayment(Integer orderId, BigDecimal penaltyAmount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(penaltyAmount);
        payment.setPayType("Penalty"); // 罚金
        payment.setPayTime(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    /**
     * 记录支付（通用方法）
     * 
     * @param orderId 订单 ID
     * @param amount 支付金额
     * @param payMethod 支付方式（Alipay, WeChat, Card）
     * @param payType 支付类型（Deposit, Final, Penalty）
     * @return 支付记录
     */
    @Transactional
    public Payment recordPayment(Integer orderId, BigDecimal amount, String payMethod, String payType) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPayMethod(payMethod);
        payment.setPayType(payType);
        payment.setPayTime(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    /**
     * 查询订单的所有支付记录
     * 
     * @param orderId 订单 ID
     * @return 支付记录列表
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByOrder(Integer orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    
    /**
     * 查询所有支付记录（管理员功能）
     * 
     * @return 所有支付记录
     */
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    /**
     * 根据 ID 查询支付记录
     * 
     * @param paymentId 支付 ID
     * @return 支付记录
     */
    @Transactional(readOnly = true)
    public Payment findById(Integer paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("支付记录不存在"));
    }
}
