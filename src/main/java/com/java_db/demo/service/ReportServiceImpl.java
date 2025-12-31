package com.java_db.demo.service;

import com.java_db.demo.dto.*;
import com.java_db.demo.entity.*;
import com.java_db.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {
    
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final PaymentRepository paymentRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final StoreRepository storeRepository;
    
    @Override
    public DashboardDTO getDashboard(LocalDateTime startDate, LocalDateTime endDate) {
        DashboardDTO dashboard = new DashboardDTO();
        
        // 关键指标
        Double totalRevenue = paymentRepository.sumTotalAmountBetweenDates(startDate, endDate);
        dashboard.setTotalRevenue(totalRevenue != null ? totalRevenue : 0.0);
        
        LocalDate startLocalDate = startDate.toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();
        Double totalMaintenanceCost = maintenanceRepository.sumTotalCostBetweenDates(startLocalDate, endLocalDate);
        dashboard.setTotalMaintenanceCost(totalMaintenanceCost != null ? totalMaintenanceCost : 0.0);
        
        dashboard.setNetProfit(dashboard.getTotalRevenue() - dashboard.getTotalMaintenanceCost());
        
        // 订单统计
        List<Order> allOrders = orderRepository.findAll();
        List<Order> periodOrders = allOrders.stream()
            .filter(o -> o.getCreatedAt().isAfter(startDate) && o.getCreatedAt().isBefore(endDate))
            .collect(Collectors.toList());
        
        dashboard.setTotalOrders(periodOrders.size());
        dashboard.setCompletedOrders((int) periodOrders.stream().filter(o -> o.getStatus() == 2).count());
        
        // 车辆统计
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        dashboard.setTotalVehicles(allVehicles.size());
        dashboard.setAvailableVehicles((int) allVehicles.stream().filter(v -> v.getStatus() == 0).count());
        dashboard.setRentedVehicles((int) allVehicles.stream().filter(v -> v.getStatus() == 1).count());
        dashboard.setMaintenanceVehicles((int) allVehicles.stream().filter(v -> v.getStatus() == 2).count());
        dashboard.setTransferVehicles((int) allVehicles.stream().filter(v -> v.getStatus() == 3).count());
        
        // 计算平均利用率
        double avgUtilization = calculateAverageUtilization(allVehicles, startDate, endDate);
        dashboard.setAverageUtilizationRate(avgUtilization);
        
        // 按分类统计车辆
        Map<String, Integer> vehicleByCategory = new HashMap<>();
        allVehicles.forEach(v -> {
            String categoryName = v.getCategory() != null ? v.getCategory().getName() : "未分类";
            vehicleByCategory.put(categoryName, vehicleByCategory.getOrDefault(categoryName, 0) + 1);
        });
        dashboard.setVehicleByCategory(vehicleByCategory);
        
        // 按门店统计车辆
        Map<String, Integer> vehicleByStore = new HashMap<>();
        allVehicles.forEach(v -> {
            String storeName = v.getStore() != null ? v.getStore().getName() : "未分配";
            vehicleByStore.put(storeName, vehicleByStore.getOrDefault(storeName, 0) + 1);
        });
        dashboard.setVehicleByStore(vehicleByStore);
        
        // 按状态统计订单
        Map<String, Integer> orderByStatus = new HashMap<>();
        orderByStatus.put("预订中", (int) periodOrders.stream().filter(o -> o.getStatus() == 0).count());
        orderByStatus.put("使用中", (int) periodOrders.stream().filter(o -> o.getStatus() == 1).count());
        orderByStatus.put("已完成", (int) periodOrders.stream().filter(o -> o.getStatus() == 2).count());
        orderByStatus.put("已取消", (int) periodOrders.stream().filter(o -> o.getStatus() == 3).count());
        dashboard.setOrderByStatus(orderByStatus);
        
        // 按门店统计收入
        List<Object[]> storeStats = orderRepository.getStoreOrderStatistics(startDate, endDate);
        Map<String, Double> revenueByStore = new HashMap<>();
        for (Object[] stat : storeStats) {
            Integer storeId = (Integer) stat[0];
            Double revenue = ((Number) stat[2]).doubleValue();
            Store store = storeRepository.findById(storeId).orElse(null);
            if (store != null) {
                revenueByStore.put(store.getName(), revenue);
            }
        }
        dashboard.setRevenueByStore(revenueByStore);
        
        // 计算增长率（对比前7天）
        LocalDateTime previousStartDate = startDate.minusDays(7);
        Double previousRevenue = paymentRepository.sumTotalAmountBetweenDates(previousStartDate, startDate);
        if (previousRevenue != null && previousRevenue > 0 && totalRevenue != null) {
            double growth = ((totalRevenue - previousRevenue) / previousRevenue) * 100;
            dashboard.setRevenueGrowthRate(Math.round(growth * 100.0) / 100.0);
        } else {
            dashboard.setRevenueGrowthRate(0.0);
        }
        
        List<Order> previousOrders = allOrders.stream()
            .filter(o -> o.getCreatedAt().isAfter(previousStartDate) && o.getCreatedAt().isBefore(startDate))
            .collect(Collectors.toList());
        int previousOrderCount = previousOrders.size();
        if (previousOrderCount > 0) {
            double orderGrowth = ((double)(periodOrders.size() - previousOrderCount) / previousOrderCount) * 100;
            dashboard.setOrderGrowthRate(Math.round(orderGrowth * 100.0) / 100.0);
        } else {
            dashboard.setOrderGrowthRate(0.0);
        }
        
        return dashboard;
    }
    
    @Override
    public List<RevenueStatisticsDTO> getRevenueStatistics(ReportPeriod period, LocalDateTime startDate, LocalDateTime endDate) {
        List<RevenueStatisticsDTO> result = new ArrayList<>();
        
        // 获取所有订单和支付记录
        List<Order> orders = orderRepository.findAll().stream()
            .filter(o -> o.getCreatedAt().isAfter(startDate) && o.getCreatedAt().isBefore(endDate))
            .collect(Collectors.toList());
        
        List<Payment> payments = paymentRepository.findAll().stream()
            .filter(p -> p.getPayTime().isAfter(startDate) && p.getPayTime().isBefore(endDate))
            .collect(Collectors.toList());
        
        // 按时间周期分组
        Map<String, List<Order>> ordersByPeriod = groupOrdersByPeriod(orders, period);
        Map<String, List<Payment>> paymentsByPeriod = groupPaymentsByPeriod(payments, period);
        
        // 生成报表
        Set<String> allPeriods = new TreeSet<>();
        allPeriods.addAll(ordersByPeriod.keySet());
        allPeriods.addAll(paymentsByPeriod.keySet());
        
        for (String periodKey : allPeriods) {
            RevenueStatisticsDTO dto = new RevenueStatisticsDTO();
            dto.setPeriod(periodKey);
            
            List<Order> periodOrders = ordersByPeriod.getOrDefault(periodKey, new ArrayList<>());
            List<Payment> periodPayments = paymentsByPeriod.getOrDefault(periodKey, new ArrayList<>());
            
            // 统计订单数
            dto.setOrderCount(periodOrders.size());
            dto.setCompletedOrderCount((int) periodOrders.stream().filter(o -> o.getStatus() == 2).count());
            dto.setCancelledOrderCount((int) periodOrders.stream().filter(o -> o.getStatus() == 3).count());
            
            // 统计收入
            double totalRevenue = periodPayments.stream()
                .filter(p -> p.getAmount() != null)
                .mapToDouble(p -> p.getAmount().doubleValue()).sum();
            dto.setTotalRevenue(totalRevenue);
            
            double depositAmount = periodPayments.stream()
                .filter(p -> "Deposit".equals(p.getPayType()))
                .filter(p -> p.getAmount() != null)
                .mapToDouble(p -> p.getAmount().doubleValue()).sum();
            dto.setDepositAmount(depositAmount);
            
            double finalAmount = periodPayments.stream()
                .filter(p -> "Final".equals(p.getPayType()))
                .filter(p -> p.getAmount() != null)
                .mapToDouble(p -> p.getAmount().doubleValue()).sum();
            dto.setFinalPaymentAmount(finalAmount);
            
            double penaltyAmount = periodPayments.stream()
                .filter(p -> "Penalty".equals(p.getPayType()))
                .filter(p -> p.getAmount() != null)
                .mapToDouble(p -> p.getAmount().doubleValue()).sum();
            dto.setPenaltyAmount(penaltyAmount);
            
            // 平均订单金额
            double avgAmount = periodOrders.isEmpty() ? 0 : 
                periodOrders.stream()
                    .filter(o -> o.getTotalAmount() != null)
                    .mapToDouble(o -> o.getTotalAmount().doubleValue()).average().orElse(0);
            dto.setAverageOrderAmount(Math.round(avgAmount * 100.0) / 100.0);
            
            result.add(dto);
        }
        
        return result;
    }
    
    @Override
    public List<VehicleUtilizationDTO> getVehicleUtilization(LocalDateTime startDate, LocalDateTime endDate) {
        List<VehicleUtilizationDTO> result = new ArrayList<>();
        
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<Order> allOrders = orderRepository.findAll();
        
        for (Vehicle vehicle : vehicles) {
            VehicleUtilizationDTO dto = new VehicleUtilizationDTO();
            dto.setVehicleId(vehicle.getId().longValue());
            dto.setLicensePlate(vehicle.getPlateNumber());
            dto.setModel(vehicle.getModel());
            dto.setCategoryName(vehicle.getCategory() != null ? vehicle.getCategory().getName() : "未分类");
            dto.setStoreName(vehicle.getStore() != null ? vehicle.getStore().getName() : "未分配");
            dto.setStatus(vehicle.getStatus());
            
            // 统计该车辆的订单
            List<Order> vehicleOrders = allOrders.stream()
                .filter(o -> o.getVehicle() != null && o.getVehicle().getId().equals(vehicle.getId()))
                .filter(o -> o.getCreatedAt().isAfter(startDate) && o.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());
            
            dto.setTotalOrders(vehicleOrders.size());
            
            // 计算租赁天数
            long totalDays = vehicleOrders.stream()
                .filter(o -> o.getStatus() == 1 || o.getStatus() == 2)  // 使用中或已完成
                .mapToLong(o -> {
                    LocalDateTime returnTime = o.getActualReturnTime() != null ? 
                        o.getActualReturnTime() : o.getEndTime();
                    return ChronoUnit.DAYS.between(o.getStartTime(), returnTime);
                })
                .sum();
            dto.setTotalRentalDays(totalDays);
            
            // 计算利用率
            long periodDays = ChronoUnit.DAYS.between(startDate, endDate);
            double utilization = periodDays > 0 ? (totalDays * 100.0 / periodDays) : 0;
            dto.setUtilizationRate(Math.round(utilization * 100.0) / 100.0);
            
            // 计算总收入
            double totalRevenue = vehicleOrders.stream()
                .filter(o -> o.getTotalAmount() != null)
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
            dto.setTotalRevenue(Math.round(totalRevenue * 100.0) / 100.0);
            
            result.add(dto);
        }
        
        // 按利用率降序排序
        result.sort((a, b) -> Double.compare(b.getUtilizationRate(), a.getUtilizationRate()));
        
        return result;
    }
    
    @Override
    public List<MaintenanceCostDTO> getMaintenanceCost(LocalDate startDate, LocalDate endDate) {
        List<MaintenanceCostDTO> result = new ArrayList<>();
        
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<Maintenance> allMaintenance = maintenanceRepository.findAll();
        List<Order> allOrders = orderRepository.findAll();
        
        for (Vehicle vehicle : vehicles) {
            // 筛选该车辆的维修记录
            List<Maintenance> vehicleMaintenance = allMaintenance.stream()
                .filter(m -> m.getVehicle() != null && m.getVehicle().getId().equals(vehicle.getId()))
                .filter(m -> !m.getStartDate().isBefore(startDate) && !m.getStartDate().isAfter(endDate))
                .collect(Collectors.toList());
            
            if (vehicleMaintenance.isEmpty()) {
                continue;  // 跳过没有维修记录的车辆
            }
            
            MaintenanceCostDTO dto = new MaintenanceCostDTO();
            dto.setVehicleId(vehicle.getId().longValue());
            dto.setLicensePlate(vehicle.getPlateNumber());
            dto.setModel(vehicle.getModel());
            dto.setCategoryName(vehicle.getCategory() != null ? vehicle.getCategory().getName() : "未分类");
            
            dto.setMaintenanceCount(vehicleMaintenance.size());
            dto.setRepairCount((int) vehicleMaintenance.stream().filter(m -> "维修".equals(m.getType())).count());
            dto.setServiceCount((int) vehicleMaintenance.stream().filter(m -> "保养".equals(m.getType())).count());
            dto.setInspectionCount((int) vehicleMaintenance.stream().filter(m -> "年检".equals(m.getType())).count());
            
            double totalCost = vehicleMaintenance.stream()
                .filter(m -> m.getCost() != null)
                .mapToDouble(m -> m.getCost().doubleValue()).sum();
            dto.setTotalCost(Math.round(totalCost * 100.0) / 100.0);
            
            double avgCost = vehicleMaintenance.isEmpty() ? 0 : totalCost / vehicleMaintenance.size();
            dto.setAverageCost(Math.round(avgCost * 100.0) / 100.0);
            
            // 计算该车辆的收入
            double revenue = allOrders.stream()
                .filter(o -> o.getVehicle() != null && o.getVehicle().getId().equals(vehicle.getId()))
                .filter(o -> o.getTotalAmount() != null)
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
            
            dto.setRevenueMinusCost(Math.round((revenue - totalCost) * 100.0) / 100.0);
            
            result.add(dto);
        }
        
        // 按总成本降序排序
        result.sort((a, b) -> Double.compare(b.getTotalCost(), a.getTotalCost()));
        
        return result;
    }
    
    @Override
    public List<OrderTrendDTO> getOrderTrend(ReportPeriod period, LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderTrendDTO> result = new ArrayList<>();
        
        List<Order> orders = orderRepository.findAll().stream()
            .filter(o -> o.getCreatedAt().isAfter(startDate) && o.getCreatedAt().isBefore(endDate))
            .collect(Collectors.toList());
        
        Map<String, List<Order>> ordersByPeriod = groupOrdersByPeriod(orders, period);
        
        for (Map.Entry<String, List<Order>> entry : ordersByPeriod.entrySet()) {
            OrderTrendDTO dto = new OrderTrendDTO();
            dto.setPeriod(entry.getKey());
            
            List<Order> periodOrders = entry.getValue();
            dto.setTotalOrders(periodOrders.size());
            dto.setPendingOrders((int) periodOrders.stream().filter(o -> o.getStatus() == 0).count());
            dto.setActiveOrders((int) periodOrders.stream().filter(o -> o.getStatus() == 1).count());
            dto.setCompletedOrders((int) periodOrders.stream().filter(o -> o.getStatus() == 2).count());
            dto.setCancelledOrders((int) periodOrders.stream().filter(o -> o.getStatus() == 3).count());
            
            double totalAmount = periodOrders.stream()
                .filter(o -> o.getTotalAmount() != null)
                .mapToDouble(o -> o.getTotalAmount().doubleValue()).sum();
            dto.setTotalAmount(Math.round(totalAmount * 100.0) / 100.0);
            
            double completionRate = periodOrders.isEmpty() ? 0 : 
                (dto.getCompletedOrders() * 100.0 / periodOrders.size());
            dto.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);
            
            double cancellationRate = periodOrders.isEmpty() ? 0 : 
                (dto.getCancelledOrders() * 100.0 / periodOrders.size());
            dto.setCancellationRate(Math.round(cancellationRate * 100.0) / 100.0);
            
            result.add(dto);
        }
        
        // 按时间排序
        result.sort(Comparator.comparing(OrderTrendDTO::getPeriod));
        
        return result;
    }
    
    @Override
    public List<StoreRevenueDTO> getStoreRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<StoreRevenueDTO> result = new ArrayList<>();
        
        List<Store> stores = storeRepository.findAll();
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        List<Order> allOrders = orderRepository.findAll();
        List<Maintenance> allMaintenance = maintenanceRepository.findAll();
        
        for (Store store : stores) {
            StoreRevenueDTO dto = new StoreRevenueDTO();
            dto.setStoreId(store.getId().longValue());
            dto.setStoreName(store.getName());
            dto.setAddress(store.getAddress());
            
            // 该门店的车辆
            List<Vehicle> storeVehicles = allVehicles.stream()
                .filter(v -> v.getStore() != null && v.getStore().getId().equals(store.getId()))
                .collect(Collectors.toList());
            dto.setVehicleCount(storeVehicles.size());
            
            // 该门店的订单（基于取车门店）
            List<Order> storeOrders = allOrders.stream()
                .filter(o -> o.getPickupStore() != null && o.getPickupStore().getId().equals(store.getId()))
                .filter(o -> o.getCreatedAt().isAfter(startDate) && o.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());
            dto.setOrderCount(storeOrders.size());
            
            // 收入统计
            double totalRevenue = storeOrders.stream()
                .filter(o -> o.getTotalAmount() != null)
                .mapToDouble(o -> o.getTotalAmount().doubleValue()).sum();
            dto.setTotalRevenue(Math.round(totalRevenue * 100.0) / 100.0);
            
            // 维修成本（该门店所有车辆的维修成本）
            Set<Integer> storeVehicleIds = storeVehicles.stream()
                .map(Vehicle::getId)
                .collect(Collectors.toSet());
            
            double maintenanceCost = allMaintenance.stream()
                .filter(m -> m.getVehicle() != null && storeVehicleIds.contains(m.getVehicle().getId()))
                .filter(m -> !m.getStartDate().isBefore(startDate.toLocalDate()) && 
                            !m.getStartDate().isAfter(endDate.toLocalDate()))
                .filter(m -> m.getCost() != null)
                .mapToDouble(m -> m.getCost().doubleValue())
                .sum();
            dto.setMaintenanceCost(Math.round(maintenanceCost * 100.0) / 100.0);
            
            // 净利润
            dto.setNetProfit(Math.round((totalRevenue - maintenanceCost) * 100.0) / 100.0);
            
            // 平均利用率
            double avgUtilization = calculateAverageUtilization(storeVehicles, startDate, endDate);
            dto.setAverageUtilization(Math.round(avgUtilization * 100.0) / 100.0);
            
            result.add(dto);
        }
        
        // 按收入降序排序
        result.sort((a, b) -> Double.compare(b.getTotalRevenue(), a.getTotalRevenue()));
        
        return result;
    }
    
    // ==================== 私有辅助方法 ====================
    
    private Map<String, List<Order>> groupOrdersByPeriod(List<Order> orders, ReportPeriod period) {
        return orders.stream().collect(Collectors.groupingBy(order -> 
            formatPeriod(order.getCreatedAt(), period)
        ));
    }
    
    private Map<String, List<Payment>> groupPaymentsByPeriod(List<Payment> payments, ReportPeriod period) {
        return payments.stream().collect(Collectors.groupingBy(payment -> 
            formatPeriod(payment.getPayTime(), period)
        ));
    }
    
    private String formatPeriod(LocalDateTime dateTime, ReportPeriod period) {
        switch (period) {
            case DAY:
                return dateTime.toLocalDate().toString();
            case WEEK:
                return dateTime.toLocalDate().toString().substring(0, 7) + "-W" + 
                       (dateTime.getDayOfYear() / 7 + 1);
            case MONTH:
                return dateTime.toLocalDate().toString().substring(0, 7);
            case YEAR:
                return String.valueOf(dateTime.getYear());
            default:
                return dateTime.toLocalDate().toString();
        }
    }
    
    private double calculateAverageUtilization(List<Vehicle> vehicles, LocalDateTime startDate, LocalDateTime endDate) {
        if (vehicles.isEmpty()) {
            return 0.0;
        }
        
        List<Order> allOrders = orderRepository.findAll();
        long periodDays = ChronoUnit.DAYS.between(startDate, endDate);
        
        if (periodDays <= 0) {
            return 0.0;
        }
        
        double totalUtilization = 0.0;
        
        for (Vehicle vehicle : vehicles) {
            List<Order> vehicleOrders = allOrders.stream()
                .filter(o -> o.getVehicle() != null && o.getVehicle().getId().equals(vehicle.getId()))
                .filter(o -> o.getCreatedAt().isAfter(startDate) && o.getCreatedAt().isBefore(endDate))
                .filter(o -> o.getStatus() == 1 || o.getStatus() == 2)
                .collect(Collectors.toList());
            
            long totalDays = vehicleOrders.stream()
                .mapToLong(o -> {
                    LocalDateTime returnTime = o.getActualReturnTime() != null ? 
                        o.getActualReturnTime() : o.getEndTime();
                    return ChronoUnit.DAYS.between(o.getStartTime(), returnTime);
                })
                .sum();
            
            totalUtilization += (totalDays * 100.0 / periodDays);
        }
        
        return totalUtilization / vehicles.size();
    }
}
