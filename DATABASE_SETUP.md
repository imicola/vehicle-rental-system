# 车辆租贷管理系统 - 数据库配置指南

## 一、前置要求

### 1. PostgreSQL 安装
- PostgreSQL 12+ 版本
- 已启动PostgreSQL服务

### 2. 创建数据库

在PostgreSQL中执行以下命令创建数据库：

```sql
-- 使用 psql 连接 PostgreSQL
psql -U postgres

-- 创建数据库
CREATE DATABASE vehicle_rental;

-- 验证创建成功
\l
```

或者使用图形化工具（如pgAdmin）创建数据库 `vehicle_rental`。

---

## 二、项目配置

### 1. 修改数据库连接信息

编辑 `src/main/resources/application.properties` 文件，根据你的PostgreSQL配置修改：

```properties
# 数据库连接信息
spring.datasource.url=jdbc:postgresql://localhost:5432/vehicle_rental
spring.datasource.username=postgres          # 你的PostgreSQL用户名
spring.datasource.password=postgres          # 你的PostgreSQL密码
```

### 2. Maven依赖安装

项目已添加以下依赖：
- `spring-boot-starter-data-jpa` - JPA ORM框架
- `postgresql` - PostgreSQL JDBC驱动
- `lombok` - 简化Java代码（可选）

运行以下命令下载依赖：
```bash
mvn clean install
```

---

## 三、自动初始化说明

项目配置了**自动初始化数据库表结构**：

```properties
spring.sql.init.mode=always
spring.sql.init.platform=postgresql
spring.jpa.hibernate.ddl-auto=update
```

**首次启动时的流程：**

1. Spring Boot 应用启动
2. 自动执行 `src/main/resources/schema.sql` 中的SQL脚本
3. 创建所有7个表（stores、categories、users、vehicles、maintenance、orders、payments）
4. 插入示例数据

> **注意**: `ddl-auto=update` 表示自动更新表结构（不删除数据）。如需完全重建，改为 `create-drop`。

---

## 四、手动初始化（可选）

如果自动初始化失败，可以手动执行SQL脚本：

### 方式1：使用psql命令行
```bash
psql -U postgres -d vehicle_rental -f src/main/resources/schema.sql
```

### 方式2：使用pgAdmin
1. 打开pgAdmin
2. 连接到 `vehicle_rental` 数据库
3. 打开 Query Tool
4. 复制并执行 `src/main/resources/schema.sql` 中的所有SQL语句

---

## 五、数据库架构概览

### 表关系图
```
stores (门店)
  ├─ vehicles (车辆) [1:N]
  │  └─ maintenance (维修记录) [1:N]
  └─ orders (订单) [1:N] (作为取车门店和还车门店)

categories (分类)
  └─ vehicles (车辆) [1:N]

users (用户)
  └─ orders (订单) [1:N]

vehicles (车辆)
  └─ orders (订单) [1:N]

orders (订单)
  └─ payments (支付) [1:N]
```

### 核心表说明

| 表名 | 说明 | 行数 |
|------|------|------|
| stores | 门店信息 | 3个示例 |
| categories | 车型分类 | 3个示例 |
| users | 系统用户 | 4个示例 |
| vehicles | 车辆信息 | 需手动添加 |
| orders | 订单信息 | 需手动添加 |
| payments | 支付记录 | 需手动添加 |
| maintenance | 维修记录 | 需手动添加 |

---

## 六、示例数据

系统初始化了以下示例数据：

### 门店
- 北京朝阳店
- 天津河西店
- 上海浦东店

### 分类
- 经济型 (100元/天)
- 舒适型 (150元/天)
- 豪华型 (250元/天)

### 用户
- admin (管理员)
- user001, user002 (普通客户)
- store_staff (门店员工)

---

## 七、常见问题

### Q1: 连接失败 "FATAL: role 'postgres' does not exist"
**A**: 确保PostgreSQL已启动，且用户名和密码正确。

### Q2: 表创建失败 "DATABASE does NOT exist"
**A**: 先在PostgreSQL中手动创建 `vehicle_rental` 数据库。

### Q3: 如何重置所有数据？
**A**: 修改 `application.properties` 中的配置为 `spring.jpa.hibernate.ddl-auto=create-drop`，重启应用。

### Q4: 如何连接远程PostgreSQL？
**A**: 修改连接字符串：
```properties
spring.datasource.url=jdbc:postgresql://192.168.1.100:5432/vehicle_rental
```

---

## 八、启动应用

```bash
# Maven方式
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

应用启动后，表示数据库连接成功。

---

## 九、验证数据库

启动应用后，可以在PostgreSQL中验证表是否创建成功：

```sql
-- 连接到vehicle_rental数据库
\c vehicle_rental

-- 查看所有表
\dt

-- 查看表结构（以stores为例）
\d stores
```

预期输出应包括：
- stores (门店表)
- categories (分类表)
- users (用户表)
- vehicles (车辆表)
- orders (订单表)
- payments (支付表)
- maintenance (维修记录表)

---

## 十、下一步

1. ✅ 数据库创建并初始化
2. ⏭️ 创建JPA Entity类映射表结构
3. ⏭️ 创建Repository接口实现数据访问
4. ⏭️ 开发业务逻辑Service层
5. ⏭️ 编写REST API Controller层

