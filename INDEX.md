# 车辆租贷管理系统 - 文档导航

## 📚 文档导航清单

### 🚀 快速开始（推荐新手先看）
1. **[QUICK_START.md](QUICK_START.md)** - 5分钟快速开始
   - 环境安装步骤
   - 5步快速启动
   - 常用命令速查

2. **[DATABASE_SETUP.md](DATABASE_SETUP.md)** - 详细配置指南
   - 前置要求和安装
   - 分步骤配置说明
   - 手动初始化方法
   - 常见问题FAQ

### 📊 数据库相关
3. **[README_DATABASE.md](README_DATABASE.md)** - 配置完成总结
   - 已完成工作总结
   - 核心表结构概览
   - 完整租车流程说明
   - 关键设计点分析

4. **[DATABASE_RELATIONS.md](DATABASE_RELATIONS.md)** - ER图和关系详解
   - 实体关系图(ER图)
   - 详细的表关系描述
   - 数据流转示例
   - 复杂查询示例

### 🛠️ 脚本和工具
5. **verify_database.sql** - 数据库验证脚本
   - 验证所有表创建成功
   - 显示表结构
   - 查询示例

6. **sample_data.sql** - 测试数据脚本
   - 15辆测试车辆
   - 6个测试订单
   - 支付和维修记录

7. **init_database.sh** - Linux/Mac自动初始化
8. **init_database.bat** - Windows自动初始化

### 💾 配置文件
9. **src/main/resources/schema.sql** - 核心数据库脚本
   - 7个表的完整DDL
   - 外键约束定义
   - 索引创建
   - 初始示例数据

10. **src/main/resources/application.properties** - Spring Boot配置
    - PostgreSQL连接信息
    - JPA/Hibernate配置
    - 连接池配置

11. **pom.xml** - Maven依赖配置
    - Spring Boot Data JPA
    - PostgreSQL JDBC驱动
    - Lombok工具库

---

## 🎯 按用途查找

### ❓ 我不知道从哪里开始
→ 先看 **[QUICK_START.md](QUICK_START.md)** 的"快速开始"部分

### 🔧 我需要安装和配置数据库
→ 参考 **[DATABASE_SETUP.md](DATABASE_SETUP.md)**

### 📋 我需要了解表的结构和关系
→ 查看 **[README_DATABASE.md](README_DATABASE.md)** 和 **[DATABASE_RELATIONS.md](DATABASE_RELATIONS.md)**

### 🚀 我准备开发后端代码
→ 阅读 **[README_DATABASE.md](README_DATABASE.md)** 的"后续开发步骤"

### 🐛 我遇到了连接问题
→ 参考 **[DATABASE_SETUP.md](DATABASE_SETUP.md)** 的"常见问题"和 **[QUICK_START.md](QUICK_START.md)** 的"常见问题排查"

### 📊 我需要添加测试数据
→ 执行 **[sample_data.sql](sample_data.sql)**

### ✔️ 我想验证数据库是否配置正确
→ 执行 **[verify_database.sql](verify_database.sql)**

### 🔗 我需要理解业务流程
→ 查看 **[DATABASE_RELATIONS.md](DATABASE_RELATIONS.md)** 的"数据流转示例"

---

## 📖 文档大纲

### 系统架构
```
车辆租贷管理系统
├── 数据库层
│   ├── 7个核心表
│   ├── 完整的外键约束
│   └── 自动索引优化
├── ORM层（Spring Boot Data JPA）
│   ├── Entity映射
│   ├── Repository接口
│   └── 查询方法
├── 业务逻辑层（Service）
│   ├── 租车业务
│   ├── 支付处理
│   └── 车辆管理
└── API层（REST Controller）
    ├── 用户接口
    ├── 订单接口
    └── 车辆接口
```

### 业务逻辑
```
异地还车流程
├── 预订阶段
│   └── 创建订单记录
├── 取车阶段
│   ├── 支付押金
│   ├── 更新订单状态
│   └── 更新车辆状态
├── 使用阶段
│   └── 车辆被租赁方使用
└── 还车阶段
    ├── 记录还车时间
    ├── 计算费用
    ├── 支付尾款
    ├── 更新订单状态
    └── 更新车辆位置和状态
```

---

## 🔑 核心概念速查

### 表名和含义
| 表名 | 中文名 | 用途 |
|------|--------|------|
| stores | 门店 | 管理所有门店信息 |
| categories | 分类 | 管理车型分类 |
| users | 用户 | 管理系统用户 |
| vehicles | 车辆 | 管理租赁车辆 |
| orders | 订单 | 管理租车订单 |
| payments | 支付 | 记录支付信息 |
| maintenance | 维修 | 记录维修保养 |

### 关键字段
| 字段 | 类型 | 说明 |
|------|------|------|
| vehicles.store_id | FK | 车当前所在门店（实时更新） |
| vehicles.status | INT | 车的状态（0空闲/1已租/2维修/3调拨） |
| orders.status | INT | 订单状态（0预订/1使用中/2已还车/3已取消） |
| orders.pickup_store_id | FK | 取车门店 |
| orders.return_store_id | FK | 还车门店（支持异地还车） |
| payments.pay_type | VARCHAR | 支付类型（Deposit/Final/Penalty） |

### 关键关系
| 关系 | 说明 |
|------|------|
| 1:N | stores ↔ vehicles，stores ↔ orders，categories ↔ vehicles |
| 1:N | vehicles ↔ maintenance，vehicles ↔ orders，users ↔ orders |
| 1:N | orders ↔ payments |

---

## ✅ 配置检查清单

按顺序检查：

- [x] PostgreSQL已安装并启动
- [x] 数据库 `vehicle_rental` 已创建
- [x] `application.properties` 中的连接信息正确
- [x] pom.xml中的依赖已下载 (`mvn clean install`)
- [x] `schema.sql` 已执行，表已创建
- [x] 初始数据已插入（3个门店、3个分类、4个用户）
- [x] 应用能够正常启动，无数据库连接错误
- [x] 可以查询表中的数据

---

## 🚀 接下来的步骤

### Phase 1: ✅ 已完成
```
[X] 数据库设计完成
[X] SQL脚本编写完成
[X] 连接配置完成
[X] 示例数据准备完成
```

### Phase 2: 待开发
```
[ ] JPA Entity类编写
    └─ 创建 User, Store, Category, Vehicle, Order, Payment, Maintenance 等Entity
    
[ ] Repository接口编写
    └─ UserRepository, VehicleRepository, OrderRepository 等
    
[ ] 自定义查询方法
    └─ 实现业务查询（如：查找空闲车、查找逾期订单等）
```

### Phase 3: 待开发
```
[ ] Service业务逻辑编写
    └─ 租车、还车、支付、维修等业务逻辑
    
[ ] 事务管理
    └─ 保证复杂操作的原子性（如：还车涉及多表更新）
    
[ ] 异常处理
    └─ 自定义异常和异常处理器
```

### Phase 4: 待开发
```
[ ] REST API开发
    └─ UserController, VehicleController, OrderController 等
    
[ ] 请求/响应格式定义
    └─ DTO类和统一响应格式
    
[ ] 参数验证
    └─ 使用 @Valid 注解验证
```

### Phase 5: 待开发
```
[ ] 单元测试
[ ] 集成测试
[ ] 端到端测试
```

---

## 📞 快速问题解答


**Q: 如何重置数据库？**
A: 在 `application.properties` 中改 `spring.jpa.hibernate.ddl-auto=create-drop`，然后重启应用（**谨慎！会删除所有数据**）

**Q: schema.sql 何时执行？**
A: Spring Boot启动时会自动执行（因为配置了 `spring.sql.init.mode=always`）

**Q: 我不想自动执行sql怎么办？**
A: 改为 `spring.sql.init.mode=never` 并手动执行：`psql -U postgres -d vehicle_rental -f src/main/resources/schema.sql`

**Q: 如何修改车的日租金？**
A: 更新 `vehicles` 表的 `daily_rate` 字段，或者修改 `categories` 表的 `basic_rate` 作为基准价

**Q: 怎样查询异地还车的车辆？**
A: 查询 `orders` 表，其中 `pickup_store_id != return_store_id`

---

## 🎓 学习路径建议

### 新手路径
1. 阅读 QUICK_START.md 快速了解
2. 按步骤完成数据库初始化
3. 查看 DATABASE_RELATIONS.md 理解架构
4. 执行 sample_data.sql 添加测试数据
5. 编写简单的查询和CRUD操作

### 进阶路径
1. 理解 ORDER BY 和 GROUP BY 复杂查询
2. 学习事务（TRANSACTION）概念
3. 理解性能优化（索引、查询优化）
4. 学习数据库设计最佳实践

### 架构设计
1. 理解ORM和JPA的优势
2. 学习Repository模式
3. 理解Service层的职责
4. 学习领域驱动设计(DDD)

---

## 📝 文件修改记录

| 文件 | 修改内容 | 时间 |
|------|----------|------|
| schema.sql | 创建7个表和初始数据 | 2024-12-24 |
| application.properties | 配置数据库连接和JPA | 2024-12-24 |
| pom.xml | 添加JPA和PostgreSQL驱动 | 2024-12-24 |
| DATABASE_SETUP.md | 详细配置指南 | 2024-12-24 |
| QUICK_START.md | 快速参考指南 | 2024-12-24 |
| README_DATABASE.md | 配置完成总结 | 2024-12-24 |
| DATABASE_RELATIONS.md | 关系图和说明 | 2024-12-24 |
| verify_database.sql | 验证脚本 | 2024-12-24 |
| sample_data.sql | 测试数据 | 2024-12-24 |
| init_database.sh | Linux/Mac脚本 | 2024-12-24 |
| init_database.bat | Windows脚本 | 2024-12-24 |

---

## 💡 提示

- 📖 **推荐按顺序阅读**: QUICK_START → DATABASE_SETUP → DATABASE_RELATIONS
- 🔍 **遇到问题先查**: DATABASE_SETUP 的常见问题章节
- 📊 **要理解业务流程**: 看 DATABASE_RELATIONS 的"数据流转示例"
- 🛠️ **要配置开发环境**: 按 QUICK_START 的5步进行
- 📝 **要写SQL查询**: 看 DATABASE_RELATIONS 的"复杂查询示例"

---

**版本**: 1.0  
**最后更新**: 2024-12-24  
**项目**: 车辆租贷管理系统  
**数据库**: PostgreSQL 12+

---