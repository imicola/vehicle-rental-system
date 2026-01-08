# 车辆 / 门店 / 分类 模块

## 范围
车辆库存管理、门店信息和车辆分类。主要组件：
- 控制器：`VehicleController`、`StoreController`、`CategoryController`（见 `src/main/java/com/java_db/demo/controller/`）。
- 服务：`VehicleService`、`StoreService`（见 `src/main/java/com/java_db/demo/service/`）。
- 仓库：`VehicleRepository`、`StoreRepository`、`CategoryRepository`（见 `src/main/java/com/java_db/demo/repository/`）。
- 实体/DTO：`Vehicle`、`Store`、`Category`、`VehicleDTO`、`StoreDTO`（见相应路径）。

## 主要功能行为
- 车辆搜索：`GET /api/vehicles?storeId&start&end` 返回属于指定门店且 `status=0`（空闲），并且在所选时间段内没有冲突订单（订单状态 0 或 1）。实现基于 JPQL 的子查询过滤冲突订单。
- 车辆管理（管理员）：新增车辆（默认 `status=0`）、更新车辆状态、按门店列出车辆、列出所有车辆、根据 ID 查询车辆详情。
- 门店管理（管理员）：新增、更新、按 ID 查询、列出所有门店。
- 分类：列出所有分类、按 ID 查询。

## 业务规则
- 车辆状态码：0 空闲、1 已租、2 维修中、3 调拨中。可租性以 `status=0` 为准。
- 新增车辆时校验车牌号唯一，并验证所关联的门店与分类存在。
- 搜索时校验时间窗口：开始时间不得晚于结束时间，且开始时间不得早于当前时间。

## 观察与不足
- 列表接口没有分页或排序，所有查询返回全集，存在性能和网络传输问题。
- `CategoryController` 在未找到分类时抛出通用异常，错误处理不一致。
- 车辆状态可被任意修改，缺少与维修记录或在租状态的交叉校验（例如在有未完成维修记录时禁止切换状态）。
- 缺少访问控制，任意调用者均可执行新增/更新操作。
- `StoreService` 提供删除门店的方法但未在控制器中暴露；且删除在存在外键关联（车辆或订单）时会失败。

## 数据注意事项
- 实体使用 `LocalDateTime` 的 `createdAt/updatedAt` 并通过 `@PrePersist/@PreUpdate` 自动维护，未实现软删除。
- 分类的 `basic_rate` 与车辆的 `daily_rate` 使用 `BigDecimal(10,2)`，与数据库模式保持一致。
