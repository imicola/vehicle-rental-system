# 车辆租贷管理系统 — 简要开发文档

## 项目概况
- 课程设计（Java-DB）：实现车辆租赁/借贷管理的基础功能（车辆、用户、订单、支付记录、统计）。
- 当前阶段：起步，架构与技术栈已确定，预留后续扩展点。

## 技术栈
- 后端：Java 21 + Spring Boot 3.1（Spring Web, Spring Data JPA）
- 数据库：PostgreSQL
- 中间库/请求：Axios（前端请求库）
- 前端：Vite + React
- 构建/包管理：Maven 或 Gradle（任选）

## 架构概览
- 前端（React + Vite） <-> 后端 REST API（Spring Boot） <-> PostgreSQL
- 可选：后端加入缓存（Redis）、消息队列（RabbitMQ/Kafka）、任务调度

## 目录/模块建议
- backend/
    - src/main/java/...（controller, service, repository, model, dto）
    - src/main/resources/application.yml
- frontend/
    - src/（pages, components, api）
- db/
    - migration/（Flyway / SQL 脚本）
- docker-compose.yml （可选）

## 快速启动（开发）
- 后端（Maven）
```
cd backend
mvn spring-boot:run
```
- 后端（Gradle）
```
cd backend
./gradlew bootRun
```
- 前端
```
cd frontend
npm install
npm run dev
```
- 本地 PostgreSQL：启动并创建数据库，配置 application.yml 中的连接字符串

## 建议的 application.yml（示例）
```yaml
spring:
    datasource:
        url: jdbc:postgresql://localhost:5432/car_rent
        username: your_user
        password: your_password
    jpa:
        hibernate:
            ddl-auto: update
        show-sql: true
```

## 基本API（示例）
- POST /api/auth/login
- GET /api/vehicles
- GET /api/vehicles/{id}
- POST /api/orders
- GET /api/users/{id}/orders

（后续补充详细接口文档）

## 数据库设计要点（简短）
- 表：users, vehicles, orders, payments, vehicle_types, audit_logs
- 主外键、索引（订单按用户和时间索引）
- 使用迁移工具（Flyway/Liquibase）

## 测试与 CI 建议
- 后端单元/集成测试（JUnit + MockMvc）
- 前端测试（Vitest / React Testing Library）
- 可配置 GitHub Actions 做构建与测试

## 后续可选功能（预留）
- 用户认证与权限（JWT、OAuth2、RBAC）
- 支付网关集成
- 在线车辆定位/地图展示
- 缓存（Redis）、异步任务、消息队列
- 容器化（Docker）、Kubernetes 部署
- 日志与监控（ELK/Prometheus）

## 开发注意事项
- 使用 DTO 隔离实体与接口
- 接口遵循 REST 语义，前后约定统一的错误响应格式
- 敏感信息使用环境变量或 secret 管理
- 定期备份数据库与迁移脚本管理

## 下一步建议
- 搭建基础项目骨架（Spring Initializr + Vite 模板）
- 建立数据库迁移脚本与基础实体
- 实现用户与车辆的增删改查 API 与前端页面

备注：如需模板工程或示例代码，可继续说明具体偏好（Maven/Gradle、认证方式等）。