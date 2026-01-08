# 报表模块

## 范围
面向分析的 API，包括仪表盘、收入统计、车辆利用率、维修成本、订单趋势和门店收入等报表。
- 控制器：`ReportController`（`src/main/java/com/java_db/demo/controller/ReportController.java`）。
- 服务：`ReportService` 与实现 `ReportServiceImpl`（`src/main/java/com/java_db/demo/service/`）。
- DTO：`DashboardDTO`、`RevenueStatisticsDTO`、`VehicleUtilizationDTO`、`MaintenanceCostDTO`、`OrderTrendDTO`、`StoreRevenueDTO`、`ReportPeriod`（见 `src/main/java/com/java_db/demo/dto/`）。

## 提供的端点
- `/api/reports/dashboard`（`startDate`, `endDate`）：统计总收入、维修成本、净利润、订单数、车辆状态分布、按分类/门店的车辆分布、按门店的收入以及与前 7 天的增长率比较。
- `/api/reports/revenue`（`period`, `startDate`, `endDate`）：按日/周/月/年粒度统计收入（押金/尾款/罚金）、订单数与平均订单金额等。
- `/api/reports/vehicle-utilization`（`startDate`, `endDate`）：统计每辆车的订单数、租赁天数、利用率、收入和状态。
- `/api/reports/maintenance-cost`（`startDate`, `endDate`）：统计每辆车的维修次数、维修成本与净收入（收入 - 成本）。
- `/api/reports/order-trend`（`period`, `startDate`, `endDate`）：按周期统计订单数量、状态分布、总金额、完成率与取消率。
- `/api/reports/store-revenue`（`startDate`, `endDate`）：按门店统计车辆数、订单数、收入、维修成本、净利润与平均利用率。

## 实现要点
- 内存聚合：许多方法先把所有订单/车辆/支付/维修数据一次性加载到内存，再用 Java 流进行过滤与聚合。该策略在数据量较大时不可扩展。
- 时间分组：按 `createdAt`（订单）和 `payTime`（支付）分组；周维度使用 `dayOfYear/7` 的简单近似，不符合 ISO 周标准。
- 利用率计算使用 `ChronoUnit.DAYS` 计算整天数，部分天数可能被舍入，且若统计周期天数为 0 会返回 0。
- 仪表盘增长比较以最近 7 天为基准，当对比周期为 0 时避免除零并返回 0。

## 风险与不足
- 未加授权保护：这些报表原本面向管理员，但当前没有访问控制，接口可被任意访问。
- 无分页：返回的数据量可能很大，影响性能与响应时间。
- 精度风险：若 `createdAt`/`payTime` 为 null（虽然 `@PrePersist` 通常会设置），或时区处理不当，会导致分组与统计偏差。
- 收入计算逻辑上可能将押金计为收入，若业务上押金应视为负债则会造成会计口径错误。

## 优化建议
- 将大部分聚合下推到数据库层（SQL 或 JPA 投影）以减少内存占用并利用索引加速查询。
- 采用 ISO 周规范进行周分组并显式处理时区。
- 对报表接口添加基于角色的访问控制（例如 `hasRole('ADMIN')`），并建议对频繁请求的指标使用缓存。
