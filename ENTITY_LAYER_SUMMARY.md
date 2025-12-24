# 实体层 (Entity Layer) 设计总结

## ✅ 已完成的实体类

所有 7 个实体类已成功创建并通过编译和启动测试。

### 📦 包位置
```
com.java_db.demo.entity
```

### 📋 实体类清单

#### 1️⃣ **Store.java** - 门店实体
- **数据库表**: `stores`
- **主键**: `id` (Integer, 自增)
- **关键字段**:
  - `name` - 门店名称 (VARCHAR 100, NOT NULL)
  - `address` - 门店地址 (VARCHAR 255)
  - `phone` - 联系电话 (VARCHAR 20)
  - `createdAt`, `updatedAt` - 时间戳 (LocalDateTime)

#### 2️⃣ **Category.java** - 车辆分类实体
- **数据库表**: `categories`
- **主键**: `id` (Integer, 自增)
- **关键字段**:
  - `name` - 分类名称 (VARCHAR 50, NOT NULL)
  - `basicRate` - 基础日租金 (BigDecimal, NOT NULL)
  - `createdAt`, `updatedAt` - 时间戳 (LocalDateTime)

#### 3️⃣ **User.java** - 用户实体
- **数据库表**: `users`
- **主键**: `id` (Integer, 自增)
- **关键字段**:
  - `username` - 用户名 (VARCHAR 50, UNIQUE, NOT NULL)
  - `password` - 密码 (VARCHAR 255, NOT NULL) ⚠️ **当前明文存储**
  - `phone` - 手机号 (VARCHAR 20, UNIQUE)
  - `role` - 角色 (Integer, 默认 0)
    - `0`: 普通客户
    - `1`: 管理员
    - `2`: 门店员工
  - `createdAt`, `updatedAt` - 时间戳 (LocalDateTime)

**⚠️ 安全提醒**:
- 密码字段当前为明文存储
- **生产环境建议**: 在 Service 层使用 `BCryptPasswordEncoder` 加密
- **课设阶段**: 可暂时明文，但需在文档中说明安全隐患

#### 4️⃣ **Vehicle.java** - 车辆实体
- **数据库表**: `vehicles`
- **主键**: `id` (Integer, 自增)
- **关键字段**:
  - `plateNumber` - 车牌号 (VARCHAR 20, UNIQUE, NOT NULL)
  - `model` - 车型 (VARCHAR 50)
  - `status` - 车辆状态 (Integer, 默认 0)
    - `0`: 空闲
    - `1`: 已租
    - `2`: 维修中
    - `3`: 调拨中
  - `dailyRate` - 日租金 (BigDecimal)
  - `createdAt`, `updatedAt` - 时间戳 (LocalDateTime)

**🔗 关联关系**:
- `@ManyToOne` → `Category` (category_id)
- `@ManyToOne` → `Store` (store_id)

#### 5️⃣ **Maintenance.java** - 维修记录实体
- **数据库表**: `maintenance`
- **主键**: `id` (Integer, 自增)
- **关键字段**:
  - `type` - 维修类型 (VARCHAR 20): 维修、保养、年检
  - `startDate` - 开始日期 (LocalDate)
  - `endDate` - 结束日期 (LocalDate)
  - `cost` - 费用 (BigDecimal)
  - `description` - 描述 (TEXT)
  - `createdAt`, `updatedAt` - 时间戳 (LocalDateTime)

**🔗 关联关系**:
- `@ManyToOne` → `Vehicle` (vehicle_id)
- **级联策略**: 删除车辆时数据库会自动删除维修记录 (ON DELETE CASCADE)

#### 6️⃣ **Order.java** - 订单实体
- **数据库表**: `orders` (⚠️ Order 是 SQL 保留字，使用 @Table 指定)
- **主键**: `id` (Integer, 自增)
- **关键字段**:
  - `orderNo` - 订单流水号 (VARCHAR 64, UNIQUE, NOT NULL)
  - `startTime` - 租赁开始时间 (LocalDateTime, NOT NULL)
  - `endTime` - 预计还车时间 (LocalDateTime, NOT NULL)
  - `actualReturnTime` - 实际还车时间 (LocalDateTime, 可为空)
  - `totalAmount` - 总金额 (BigDecimal)
  - `status` - 订单状态 (Integer, 默认 0)
    - `0`: 预订
    - `1`: 使用中
    - `2`: 已还车
    - `3`: 已取消
  - `createdAt`, `updatedAt` - 时间戳 (LocalDateTime)

**🔗 关联关系**:
- `@ManyToOne` → `User` (user_id)
- `@ManyToOne` → `Vehicle` (vehicle_id)
- `@ManyToOne` → `Store` (pickup_store_id) - 取车门店
- `@ManyToOne` → `Store` (return_store_id) - 还车门店

#### 7️⃣ **Payment.java** - 支付记录实体
- **数据库表**: `payments`
- **主键**: `id` (Integer, 自增)
- **关键字段**:
  - `amount` - 支付金额 (BigDecimal, NOT NULL)
  - `payMethod` - 支付方式 (VARCHAR 20): Alipay, WeChat, Card
  - `payType` - 支付类型 (VARCHAR 20):
    - `Deposit`: 押金
    - `Final`: 尾款
    - `Penalty`: 罚金
  - `payTime` - 支付时间 (LocalDateTime)
  - `createdAt`, `updatedAt` - 时间戳 (LocalDateTime)

**🔗 关联关系**:
- `@ManyToOne` → `Order` (order_id)
- **级联策略**: 删除订单时数据库会自动删除支付记录 (ON DELETE CASCADE)

---

## 🏗️ 技术实现细节

### 核心注解使用
```java
@Entity                                    // 标记为 JPA 实体
@Table(name = "table_name")               // 指定数据库表名
@Data                                      // Lombok: 自动生成 Getter/Setter
@NoArgsConstructor                         // Lombok: 无参构造函数
@AllArgsConstructor                        // Lombok: 全参构造函数
@Id                                        // 主键标识
@GeneratedValue(strategy = IDENTITY)      // 主键自增策略
@Column(name = "column_name")             // 列映射
@ManyToOne(fetch = FetchType.LAZY)        // 多对一懒加载
@JoinColumn(name = "fk_column")           // 外键列
@PrePersist / @PreUpdate                  // 生命周期回调
```

### 数据类型映射
| PostgreSQL 类型 | Java 类型 | 说明 |
|----------------|-----------|------|
| SERIAL | Integer | 自增主键 |
| VARCHAR(n) | String | 字符串 |
| TIMESTAMP | LocalDateTime | 时间戳 |
| DATE | LocalDate | 日期 |
| DECIMAL(10,2) | BigDecimal | 高精度金额 |
| TEXT | String | 长文本 |
| INT | Integer | 整数 |

### 时间戳自动管理
所有实体类使用 `@PrePersist` 和 `@PreUpdate` 自动维护时间戳：
```java
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```

---

## 🔗 实体关系图 (ER Summary)

```
Store (1) ━━━━━━━━━━━━━━━━━> (N) Vehicle
Category (1) ━━━━━━━━━━━━━━> (N) Vehicle
Vehicle (1) ━━━━━━━━━━━━━━━> (N) Maintenance
Vehicle (1) ━━━━━━━━━━━━━━━> (N) Order
User (1) ━━━━━━━━━━━━━━━━━> (N) Order
Store (1) ━━━━━━━━━━━━━━━━> (N) Order (取车门店)
Store (1) ━━━━━━━━━━━━━━━━> (N) Order (还车门店)
Order (1) ━━━━━━━━━━━━━━━━> (N) Payment
```

---

## ✅ 验证结果

### 编译验证
```bash
mvn clean compile
```
**结果**: ✅ BUILD SUCCESS

### 启动验证
```bash
./mvnw spring-boot:run
```
**结果**: ✅ 应用成功启动，JPA 实体管理器正常初始化
```
INFO: Initialized JPA EntityManagerFactory for persistence unit 'default'
INFO: Started DemoApplication in 2.427 seconds
```

### 数据库连接验证
```
Database JDBC URL: jdbc:postgresql://localhost:5432/vehicle_rental
Database version: 18.1
Database dialect: PostgreSQLDialect
```

---

## 📝 下一步建议

### 1️⃣ **创建 Repository 层**
为每个实体创建 Spring Data JPA Repository 接口：
```java
public interface StoreRepository extends JpaRepository<Store, Integer> {
    // 自定义查询方法
}
```

### 2️⃣ **创建 Service 层**
实现业务逻辑，特别是：
- 用户密码加密 (BCryptPasswordEncoder)
- 订单状态流转控制
- 车辆状态管理
- 支付记录关联

### 3️⃣ **创建 Controller 层 (REST API)**
设计 RESTful API 端点：
```
POST   /api/users/register     - 用户注册
POST   /api/users/login        - 用户登录
GET    /api/vehicles           - 车辆列表
POST   /api/orders             - 创建订单
GET    /api/orders/{id}        - 订单详情
POST   /api/payments           - 支付记录
```

### 4️⃣ **数据验证**
添加 Bean Validation 注解：
```java
@NotBlank(message = "用户名不能为空")
@Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
private String username;

@Email(message = "邮箱格式不正确")
private String email;
```

### 5️⃣ **异常处理**
创建全局异常处理器处理业务异常和数据库约束异常。

### 6️⃣ **API 文档**
集成 Swagger/OpenAPI 自动生成 API 文档。

---

## 🔒 安全性注意事项

### ⚠️ 密码存储
**当前状态**: 明文存储（不安全）

**推荐方案**:
```java
// 在 Service 层注入密码编码器
@Autowired
private PasswordEncoder passwordEncoder;

// 注册时加密
user.setPassword(passwordEncoder.encode(plainPassword));

// 登录验证
passwordEncoder.matches(inputPassword, user.getPassword());
```

**配置类**:
```java
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 📋 课设文档建议说明
> "本系统密码字段采用明文存储以简化开发流程，生产环境应使用 BCrypt 或 Argon2 算法进行单向加密。建议添加密码强度验证（至少8位，包含字母数字）和登录失败次数限制。"

---

## 📊 技术栈总结

| 组件 | 版本/技术 |
|------|----------|
| Java | 21 (OpenJDK) |
| Spring Boot | 4.0.1 |
| Hibernate ORM | 7.2.0.Final |
| PostgreSQL | 18.1 |
| Lombok | Latest |
| Maven | Latest |

---

**创建时间**: 2025-12-24  
**状态**: ✅ 实体层已完成并验证通过
