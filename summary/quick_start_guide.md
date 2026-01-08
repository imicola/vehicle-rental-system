# 车辆租赁系统 - 快速上手指南

## 项目全景图

这是一个基于 **Spring Boot + React + PostgreSQL** 的全栈车辆租赁管理系统，实现了完整的用户租车流程和管理员后台管理功能。系统采用现代化的前后端分离架构，后端使用Spring Boot提供RESTful API服务，前端使用React构建用户界面，数据库采用PostgreSQL进行数据持久化，并集成了JWT身份认证、Swagger API文档、车辆状态管理、订单处理、报表统计等核心功能。

## 核心架构拆解

### 后端架构（Java Spring Boot）

- **Controller层** (`/src/main/java/com/java_db/demo/controller/`): 处理HTTP请求，提供RESTful API接口，包括AuthController、OrderController、VehicleController、StoreController、MaintenanceController、ReportController等
- **Service层** (`/src/main/java/com/java_db/demo/service/`): 包含业务逻辑，处理复杂的业务规则，如OrderService（订单管理）、VehicleService（车辆管理）、ReportService（报表统计）等
- **Entity层** (`/src/main/java/com/java_db/demo/entity/`): 数据库实体类，定义数据模型，如User、Vehicle、Order、Store、Category等
- **Repository层** (`/src/main/java/com/java_db/demo/repository/`): 数据访问层，提供数据库操作接口，使用Spring Data JPA自动生成SQL
- **DTO层** (`/src/main/java/com/java_db/demo/dto/`): 数据传输对象，用于前后端数据交换
- **Config层** (`/src/main/java/com/java_db/demo/config/`): 系统配置，包括安全配置、Swagger配置等
- **Exception层** (`/src/main/java/com/java_db/demo/exception/`): 自定义异常处理
- **Util层** (`/src/main/java/com/java_db/demo/util/`): 工具类，如JWT令牌生成器

### 前端架构（React + TypeScript）

- **Pages** (`/frontend/src/pages/`): 页面组件，包括用户端页面（Home、VehicleList、MyOrders等）和管理员后台页面（Dashboard、Orders、Vehicles等）
- **Components** (`/frontend/src/components/`): 可复用UI组件，如Layout、RentalModal等
- **API** (`/frontend/src/api/`): API请求封装，与后端RESTful API进行交互
- **Context** (`/frontend/src/context/`): React Context，用于全局状态管理，如AuthContext
- **Types** (`/frontend/src/types/`): TypeScript类型定义
- **Styles** (`/frontend/src/styles/`): 全局样式定义

## 关键业务流

### 1. 用户租车流程
- **前端请求**: 用户在VehicleList页面选择门店、时间并点击"立即租车"
- **API调用**: 前端调用`/api/vehicles`搜索可用车辆，再调用`/api/orders`创建订单
- **后端处理**: 
  - VehicleService验证车辆状态和时间冲突
  - OrderService计算租金、生成订单号、更新车辆状态为"已租"
  - 使用事务保证数据一致性，防止并发预订
- **数据库存储**: 生成Order记录，更新Vehicle状态字段

### 2. 车辆状态管理流程
- **前端请求**: 管理员在后台管理界面更新车辆状态
- **API调用**: 调用`/api/vehicles/{id}/status`接口
- **后端处理**: VehicleService验证参数，更新车辆状态字段
- **数据库存储**: 更新vehicles表中对应记录的状态字段

### 3. 订单还车流程
- **前端请求**: 用户在订单详情页点击"还车"按钮
- **API调用**: 调用`/api/orders/{id}/return`接口
- **后端处理**: 
  - OrderService计算逾期天数和罚金
  - 更新订单状态为"已还车"，更新实际还车时间
  - 恢复车辆状态为"空闲"，处理异地还车逻辑
- **数据库存储**: 更新orders表和vehicles表

### 4. 报表统计流程
- **前端请求**: 管理员访问Dashboard或Reports页面
- **API调用**: 调用`/api/reports/dashboard`或相关统计接口
- **后端处理**: ReportService执行复杂的数据聚合和计算，包括收入统计、车辆利用率、维修成本分析等
- **数据库存储**: 从多个表（orders、vehicles、payments、maintenance等）查询数据并进行统计分析

## 重点代码段解析

### 1. 订单创建服务方法 - OrderService.createOrder()

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public Order createOrder(OrderDTO orderDTO) {
    // 1. 参数验证
    if (orderDTO.getStartTime().isAfter(orderDTO.getEndTime())) {
        throw new BusinessException("开始时间不能晚于结束时间");
    }
    
    // 2. 检查时间冲突（核心逻辑）
    List<Order> conflictingOrders = orderRepository.findConflictingOrders(
            vehicle.getId(),
            orderDTO.getStartTime(),
            orderDTO.getEndTime(),
            Arrays.asList(0, 1) // 预订和使用中的订单
    );
    
    if (!conflictingOrders.isEmpty()) {
        throw new BusinessException("该车辆在指定时间段已被预订");
    }
    
    // 3. 计算订单金额
    long days = Duration.between(orderDTO.getStartTime(), orderDTO.getEndTime()).toDays();
    if (days == 0) {
        days = 1; // 至少按1天计算
    }
    BigDecimal totalAmount = vehicle.getDailyRate().multiply(BigDecimal.valueOf(days));
    
    // 4. 创建订单并更新车辆状态
    Order order = new Order();
    // ... 设置订单属性
    vehicle.setStatus(1); // 已租
    vehicleRepository.save(vehicle);
    
    return orderRepository.save(order);
}
```

**作用**: 处理用户租车的核心业务逻辑，确保数据一致性和并发安全。
**参数含义**: orderDTO包含用户ID、车辆ID、取还车门店ID、时间等信息。
**设计原因**: 使用事务隔离级别READ_COMMITTED防止并发预订冲突，通过时间冲突检查避免重复预订。

### 2. 可用车辆搜索方法 - VehicleService.searchAvailableVehicles()

```java
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
```

**作用**: 根据门店和时间段搜索可租用车辆，是用户端核心功能。
**参数含义**: storeId指定门店，startTime和endTime指定租赁时间段。
**设计原因**: 通过复杂的JPQL查询实现精确的时间冲突检测，确保返回的车辆在指定时间段内真正可用。

### 3. 报表统计方法 - ReportServiceImpl.getDashboard()

```java
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
    
    return dashboard;
}
```

**作用**: 生成系统仪表盘数据，包括收入、订单、车辆等关键业务指标。
**参数含义**: startDate和endDate定义统计时间段。
**设计原因**: 通过复杂的数据聚合和关联查询，为管理员提供全面的业务洞察，支持决策制定。

## 模拟 Q&A（防坑指南）

### Q1: 如何防止同一辆车被多人同时预订？
**满分回答**: 系统采用多种机制防止重复预订：
1. 在OrderService.createOrder()方法中使用@Transactional(isolation = Isolation.READ_COMMITTED)事务隔离级别，防止并发问题
2. 通过vehicleRepository.findAvailableVehicles()方法执行精确的时间冲突检测
3. 在创建订单前检查车辆状态，确保车辆处于"空闲"状态
4. 使用数据库唯一约束和业务逻辑双重保障

### Q2: 订单金额是如何计算的？逾期罚金如何处理？
**满分回答**: 
- 订单金额 = 车辆日租金 × 租赁天数，不足一天按一天计算
- 逾期罚金 = 日租金 × 超期天数 × 1.5倍费率
- 在returnVehicle()方法中计算实际还车时间与预计还车时间的差异
- 系统自动将罚金加入订单总金额，确保费用计算准确

### Q3: 系统如何处理异地还车？
**满分回答**: 
- 在returnVehicle()方法中，如果实际还车门店与原定还车门店不同，系统会更新车辆的所属门店
- 更新逻辑：`if (!vehicle.getStore().getId().equals(returnStoreId)) { vehicle.setStore(returnStore); }`
- 这样确保车辆信息与实际位置一致，便于后续调度和管理

### Q4: 报表统计功能是如何实现的？
**满分回答**: 
- ReportService实现了多维度统计功能，包括收入统计、车辆利用率、维修成本分析等
- 使用复杂的数据聚合查询，从多个表（orders、payments、maintenance等）获取数据
- 通过DashboardDTO封装统计结果，提供直观的数据展示
- 支持按时间段、门店、车辆类型等维度进行统计分析

### Q5: JWT身份认证是如何实现的？
**满分回答**: 
- 使用JwtProvider类生成和验证JWT令牌
- 在SecurityConfig中配置JWT过滤器，拦截请求并验证令牌
- 用户登录后生成包含用户信息的JWT令牌，后续请求携带该令牌进行身份验证
- 令牌包含过期时间，确保安全性，支持用户会话管理