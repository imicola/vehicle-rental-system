# 订单与支付模块

## 范围
订单整个生命周期（创建、取消、还车）以及支付记录（押金、尾款、罚金）。主要组件如下：
- 控制器：`OrderController`、`PaymentController`（见 `src/main/java/com/java_db/demo/controller/`）。
- 服务：`OrderService`、`PaymentService`（见 `src/main/java/com/java_db/demo/service/`）。
- 仓库：`OrderRepository`、`PaymentRepository`（见 `src/main/java/com/java_db/demo/repository/`）。
- 实体/DTO：`Order`、`OrderDTO`、`Payment`（见相应路径）。

## 订单流程
1. 创建订单（`POST /api/orders`）：校验时间区间，查询用户/车辆/取车门店/还车门店，确认车辆 `status=0`（空闲），检查时间冲突（订单状态为 0 或 1 的冲突订单），计算 `totalAmount = dailyRate * 天数（至少 1 天）`，订单状态设置为 0（预订），把车辆状态置为 1（已租），保存订单。订单号格式：`ORD-{timestamp}-{UUID8}`。事务隔离级别为 `READ_COMMITTED`。
2. 还车（`POST /api/orders/{id}/return?storeId`）：记录实际还车时间为当前时间，若逾期按日租金的 1.5 倍计算逾期罚金（逾期天数最小计为 1 天），把罚金加到订单总额，订单状态设为 2（已还车），若异地还车则更新车辆所属门店，恢复车辆状态为 0（空闲）。
3. 取消订单（`POST /api/orders/{id}/cancel`）：若订单未完成且未取消，设置订单状态为 3（已取消），并恢复车辆状态为 0（空闲）。
4. 查询接口：按 ID、按订单号、查询所有订单，以及 `GET /api/orders/my?userId` 查询某用户订单（当前未绑定鉴权主体）。

## 支付流程
- `POST /api/payments/deposit?orderId`：创建押金记录，押金金额 = 日租金 × 3，记录时间为当前时间。
- `POST /api/payments/final?orderId&amount`：创建尾款支付记录，金额由客户端传入。
- `POST /api/payments/penalty?orderId&amount`：创建罚金支付记录，金额由客户端传入。
- `GET /api/payments/order/{orderId}` 或 `/api/payments/all` 用于查询支付记录。

## 业务耦合与要点
- 支付记录不会在订单创建或还车时自动生成或自动结算，必须由调用者显式调用支付相关接口。
- 逾期罚金会写入订单总额，但不会自动生成相应的罚金支付记录。
- 支付记录与订单是否已结清之间没有直接的状态绑定或校验逻辑。
- 车辆状态是决定可租性的关键（在创建/取消/还车流程中被修改）。

## 风险与不足
- 并发控制：仅使用 `READ_COMMITTED` 隔离级别且未加锁，可能在并发场景下导致重复预订（两个事务都读到车辆为空闲然后都下单）。
- 参数信任：接口信任客户端传入的 `userId`、`storeId` 和支付金额，缺少归属校验与权限控制，存在越权与伪造风险。
- 无法阻止他人对他人订单进行还车或取消操作（缺少主体绑定与权限校验）。
- 缺少取消退款逻辑，也没有支付状态（已支付/未支付）追踪。
- 时间比较基于服务器时区；逾期计算以 `Duration.toDays` 为单位，若发生部分日期差异可能被向下舍入，然后又被业务逻辑最小化为 1 天计费，需注意边界行为。
- 列表接口缺少分页，返回全集在数据量大时会导致性能问题。

## 建议改进
- 在可用性检查处加入乐观或悲观锁，或在数据库层增加约束以防止重复预订。
- 使用 JWT 中的主体（userId）替代客户端传入的 `userId`，并基于角色做访问控制。
- 将支付创建流程与订单生命周期更紧密地结合，添加支付状态字段以跟踪结算情况。
- 把逾期罚金作为独立字段，并在还车时自动生成罚金支付记录以保证账务一致性。
