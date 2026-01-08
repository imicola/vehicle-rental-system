# 维修模块

## 范围
车辆的维修/保养/年检记录管理，并与车辆状态联动。主要组件：
- 控制器：`MaintenanceController`（`src/main/java/com/java_db/demo/controller/MaintenanceController.java`）。
- 服务：`MaintenanceService`（`src/main/java/com/java_db/demo/service/MaintenanceService.java`）。
- 仓库：`MaintenanceRepository`（`src/main/java/com/java_db/demo/repository/MaintenanceRepository.java`）。
- 实体/DTO：`Maintenance`、`MaintenanceCostDTO`（见相应路径）。

## 功能行为
- 创建维修记录（`POST /api/maintenance`）：将车辆状态置为 2（维修中），保存车辆、维修类型、开始日期、费用和描述等信息。
- 完成维修（`PUT /api/maintenance/{id}/complete`）：设置完成日期（`endDate` 为当前日期），并把车辆状态恢复为 0（空闲）。
- 查询功能：按车辆 id 查询维修历史，或查询所有维修记录。

## 业务规则
- 维修操作会直接修改车辆状态；系统没有在创建维修前检查车辆是否存在正在进行的订单或冲突预订。
- 维修类型在代码中以字符串表示（例如：维修/保养/年检），未使用枚举或受限值集。

## 风险与不足
- 未校验：允许在车辆处于已租状态时创建维修记录，可能导致订单与维修流程冲突。
- 无并发/重叠检查：未阻止对同一辆车重复创建多个重叠的维修记录。
- 控制器在创建维修记录时未使用 DTO+`@Valid` 做严格参数校验，非法或缺失参数可能导致 500 错误。
- 无访问控制，任意调用者均可创建或完成维修并改变车辆状态。

## 数据说明
- 费用字段使用 `BigDecimal(10,2)`，开始/结束日期使用 `LocalDate`，审计时间使用 `LocalDateTime`。
- `MaintenanceCostDTO` 在报表模块中用于聚合每辆车的维修次数与成本并与收入比较。
