# 后端概览

## 技术栈与入口
- Spring Boot 应用入口： [src/main/java/com/java_db/demo/DemoApplication.java](src/main/java/com/java_db/demo/DemoApplication.java)。
- 核心依赖：Spring Web、Spring Data JPA、Spring Security（当前仅注册了密码编码器 Bean）、Lombok、JWT 库、springdoc-openapi（用于生成 Swagger/OpenAPI 文档）。
- 数据库：PostgreSQL，连接配置位于 [src/main/resources/application.properties](src/main/resources/application.properties)；数据库模式与初始数据由 [src/main/resources/schema.sql](src/main/resources/schema.sql) 在启动时导入，包含 7 个核心表：`stores`、`categories`、`users`、`vehicles`、`maintenance`、`orders`、`payments`。
- 密码哈希采用 BCrypt，配置在 [src/main/java/com/java_db/demo/config/SecurityConfig.java](src/main/java/com/java_db/demo/config/SecurityConfig.java)；API 文档配置在 [src/main/java/com/java_db/demo/config/SwaggerConfig.java](src/main/java/com/java_db/demo/config/SwaggerConfig.java)。

## 领域模型速览
- `User`：角色说明 0=客户，1=管理员，2=门店员工，实体见 [src/main/java/com/java_db/demo/entity/User.java](src/main/java/com/java_db/demo/entity/User.java)。
- 门店与车辆分类：`Store`、`Category`、`Vehicle`，车辆有状态码：0 空闲、1 已租、2 维修中、3 调拨中（详见相应实体）。
- 业务交易：`Order` 包含取车/还车门店与时间区间；`Payment` 与订单关联并区分押金/尾款/罚金；`Maintenance` 与车辆关联记录维修/保养信息。

## 横切行为要点
- 事务：服务层大量使用 `@Transactional`；`OrderService#createOrder` 使用 `READ_COMMITTED` 隔离级别，但未使用悲观锁或数据库级约束来完全防止并发预订竞争。
- 校验：请求 DTO 使用 Jakarta Validation 注解，控制器使用 `@Valid` 做基本参数校验。
- 时间处理：使用 `LocalDateTime` / `LocalDate`，没有显式的时区处理，时间比较均基于服务器时区。
- 状态语义：车辆状态驱动可租性；维护操作会把车辆设为维修中（2）；订单创建会把车辆设为已租（1），取消/还车时恢复为 0。

## 安全现状
- 项目包含 JWT 工具类 [src/main/java/com/java_db/demo/util/JwtProvider.java](src/main/java/com/java_db/demo/util/JwtProvider.java) 用于生成和解析 Token（HS256）。
- 缺失项：没有注册 JWT 验证过滤器、没有配置 HTTP 安全规则或基于角色的访问控制，因此虽能签发 Token，但请求端并未被强制验证，控制器接口当前实际处于公开可访问状态。

## 可观察性与 API 文档
- Swagger UI 路径为 `/swagger-ui.html`，OpenAPI 文档由 `springdoc` 提供，相关配置在 `SwaggerConfig` 中。
- 日志使用默认配置，未见专门的审计日志或集中监控集成。

## 数据初始化
- `schema.sql` 中包含示例门店、分类与用户（示例密码为 BCrypt 值），项目启动时会按配置自动执行 SQL 导入。
- JPA 设置 `spring.jpa.hibernate.ddl-auto=update`，配合 SQL 初始化脚本共同维护模式与初始数据。

## 主要不足与风险
- 虽然实现了 JWT 的签发，但未在请求侧强制认证与授权，存在接口被未授权访问的风险。
- 预订并发控制较弱，在高并发下可能产生重叠订单（竞态条件）。
- 控制器广泛接受客户端提供的 `userId` 等标识，缺少基于令牌的主体绑定与权限校验，容易越权操作。
- 报表与某些查询把整表数据加载到内存后在 Java 端聚合，无法良好扩展到大量数据场景，且可能带来性能与内存压力。
