# 车辆租贷管理系统 - 数据库关系图

## ER图 (实体关系图)

```
                    ┌─────────────────┐
                    │    stores       │
                    │   (门店信息)     │
                    ├─────────────────┤
                    │ ⭐ id (PK)      │
                    │   name          │
                    │   address       │
                    │   phone         │
                    └─────────────────┘
                           △
                    ┌──────┴──────┐
                    │             │
                    │             │
        ┌───────────┴──┐    ┌─────┴──────────┐
        │              │    │                │
        │              │    │                │
   ┌────┴────┐    ┌────┴────┐         ┌─────┴───────┐
   │vehicles │    │  orders │         │return_store │
   │(车辆)   │    │ (订单)  │         │  (还车点)   │
   └────┬────┘    └────┬────┘         └─────────────┘
        │              │
        │              │
     ┌──┴──┐           │
     │  ├──┼───────────┘ (user_id)
     │  │  │
     │  │  │
  ┌──┴┐ │  │ ┌───────────────┐
  │  │ │  └─┤  users        │
  │  │ │    │  (用户)        │
  │  │ │    ├───────────────┤
  │  │ │    │⭐ id (PK)     │
  │  │ │    │  username     │
  │  │ │    │  password     │
  │  │ │    │  phone        │
  │  │ │    │  role         │
  │  │ │    └───────────────┘
  │  │ │
  │  │ │ (vehicle_id)
  │  │ │
  │  │ └────────────────────┐
  │  │                      │
  │  │      ┌───────────────┴──────────┐
  │  │      │                          │
  │  ├──────┤  orders                  │
  │  │      │  (订单)                  │
  │  │      ├───────────────────────────┤
  │  │      │⭐ id (PK)               │
  │  │      │  order_no               │
  │  │      │  user_id (FK)           │
  │  │      │  vehicle_id (FK)        │
  │  │      │  pickup_store_id (FK)   │
  │  │      │  return_store_id (FK)   │
  │  │      │  start_time             │
  │  │      │  end_time               │
  │  │      │  actual_return_time     │
  │  │      │  total_amount           │
  │  │      │  status                 │
  │  │      └───────────────┬──────────┘
  │  │                      │
  │  │                      │ (order_id)
  │  │                      │
  │  │      ┌───────────────┴──────┐
  │  │      │                      │
  │  │      │  payments            │
  │  │      │  (支付记录)          │
  │  │      ├──────────────────────┤
  │  │      │⭐ id (PK)           │
  │  │      │  order_id (FK)      │
  │  │      │  amount             │
  │  │      │  pay_method         │
  │  │      │  pay_type           │
  │  │      │  pay_time           │
  │  │      └──────────────────────┘
  │  │
  │  │
  ├──┼──────────────────────────────┐
  │  │                              │
  │  │      ┌──────────────────────┐│
  │  │      │   vehicles           ││
  │  │      │   (车辆)             ││
  │  │      ├──────────────────────┤│
  │  │      │⭐ id (PK)           ││
  │  │      │  plate_number       ││
  │  │      │  model              ││
  │  │      │  category_id (FK) ──┤┼────────┐
  │  │      │  store_id (FK) ─────┤┤        │
  │  │      │  status             ││        │
  │  │      │  daily_rate         ││        │
  │  │      └──────────┬───────────┘│        │
  │  │                 │            │        │
  │  │                 │ (vehicle_id)        │
  │  │                 │            │        │
  │  │      ┌──────────┴───────────┐│        │
  │  │      │                      ││        │
  │  │      │  maintenance        ││        │
  │  │      │  (维修记录)         ││        │
  │  │      ├──────────────────────┤│        │
  │  │      │⭐ id (PK)           ││        │
  │  │      │  vehicle_id (FK) ───┼┘        │
  │  │      │  type               │         │
  │  │      │  start_date         │         │
  │  │      │  end_date           │         │
  │  │      │  cost               │         │
  │  │      │  description        │         │
  │  │      └──────────────────────┘         │
  │  │                                       │
  │  └───────────────────────────────────────┘
  │
  └──────────────────────────────────┐
                                     │
                  ┌──────────────────┴──────┐
                  │                         │
              ┌───┴────────┐            ┌──┴─────────┐
              │categories  │            │ (分类)     │
              │(分类)      │            ├────────────┤
              ├────────────┤            │⭐ id (PK) │
              │⭐ id (PK) │            │  name      │
              │  name      │            │  basic_rate
              │  basic_rate            └────────────┘
              └────────────┘
```

---

## 关系描述

### 1. Stores (门店) 的关系

```
stores (1) ──────────── (N) vehicles (车辆)
  ↓                          ↑
  ├─ store_id FK                └─ 当前所在门店

stores (1) ──────────── (N) orders (订单)
  ├─ pickup_store_id FK         └─ 取车门店
  └─ return_store_id FK         └─ 还车门店
```

**说明**: 
- 一个门店拥有多辆车（在该门店停放）
- 一个门店可以有多个订单作为取车点
- 一个门店可以有多个订单作为还车点

---

### 2. Categories (分类) 的关系

```
categories (1) ──────────── (N) vehicles
    ↑                            │
    └────── category_id FK ──────┘

```

**说明**:
- 一个分类包含多辆车
- 每辆车属于一个分类（用于管理和计价）

---

### 3. Users (用户) 的关系

```
users (1) ──────────── (N) orders
  ↑                        │
  └───── user_id FK ───────┘
```

**说明**:
- 一个用户可以创建多个订单
- 每个订单对应一个用户

---

### 4. Vehicles (车辆) 的关系 - 最复杂

```
vehicles (1) ──────────── (N) orders
              ↑                 │
              └─ vehicle_id FK ─┘

vehicles (1) ──────────── (N) maintenance
              ↑                    │
              └─ vehicle_id FK ────┘

vehicles (N) ────────────── (1) categories
     │                          ↑
     └──── category_id FK ──────┘

vehicles (N) ────────────── (1) stores
     │                         ↑
     └──── store_id FK ────────┘
```

**说明**:
- 车辆有多条订单记录（被出租多次）
- 车辆有多条维修记录
- 车辆属于一个分类
- 车辆当前在一个门店

---

### 5. Orders (订单) 的关系 - 核心业务表

```
orders (1) ───────────── (N) payments
  ↑                          │
  └───── order_id FK ────────┘

orders (N) ────────────── (1) users
  │                          ↑
  └───── user_id FK ─────────┘

orders (N) ────────────── (1) vehicles
  │                          ↑
  └───── vehicle_id FK ──────┘

orders (N) ────────────── (1) stores (取车)
  │                          ↑
  └─ pickup_store_id FK ─────┘

orders (N) ────────────── (1) stores (还车)
  │                          ↑
  └─ return_store_id FK ─────┘
```

**说明**:
- 一个订单有多条支付记录
- 一个订单关联一个用户
- 一个订单关联一辆车
- 一个订单关联一个取车门店
- 一个订单关联一个还车门店（异地还车）

---

### 6. Payments (支付) 的关系

```
payments (N) ────────────── (1) orders
   │                            ↑
   └───── order_id FK ──────────┘
```

**说明**:
- 一条支付记录属于一个订单
- 支持多次分阶段支付（押金、尾款、罚金等）

---

### 7. Maintenance (维修) 的关系

```
maintenance (N) ────────────── (1) vehicles
      │                            ↑
      └───── vehicle_id FK ────────┘
```

**说明**:
- 一条维修记录属于一辆车
- 记录车辆的维修、保养、年检等信息

---

## 数据流转示例

### 完整租车流程的数据变化

#### 1️⃣ **预订阶段**

```
创建订单记录：
INSERT INTO orders (order_no, user_id, vehicle_id, pickup_store_id, return_store_id, ..., status)
VALUES ('ORD20240115001', 2, 8, 1, 2, ..., 0);

此时数据状态：
┌─────────────────────────────────────────────────────────┐
│ vehicles 表中：                                          │
│   id=8  │  plate_number='京A34568'  │  status=0 (空闲)  │
│         │  store_id=1 (北京)        │                   │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ orders 表中：                                            │
│   order_no='ORD20240115001'  │  status=0 (预订)        │
│   user_id=2                   │                         │
│   vehicle_id=8                │                         │
│   pickup_store_id=1 (北京)    │                         │
│   return_store_id=2 (天津)    │                         │
└─────────────────────────────────────────────────────────┘
```

#### 2️⃣ **取车阶段**

```
更新订单：status 0→1，创建支付记录
UPDATE orders SET status=1 WHERE id=1;
INSERT INTO payments (order_id, amount, pay_type) VALUES (1, 100.00, 'Deposit');

更新车辆：status 0→1
UPDATE vehicles SET status=1 WHERE id=8;

数据状态：
┌─────────────────────────────────────────────────────────┐
│ vehicles 表：                                            │
│   id=8  │  status=1 (已租)                              │
│         │  store_id=1 (北京) [未变]                     │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ orders 表：                                              │
│   order_no='ORD20240115001'  │  status=1 (使用中)      │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ payments 表：                                            │
│   order_id=1  │  amount=100  │  pay_type='Deposit'    │
└─────────────────────────────────────────────────────────┘
```

#### 3️⃣ **还车阶段**

```
更新订单：status 1→2，记录实际还车时间，创建尾款支付
UPDATE orders 
SET status=2, 
    actual_return_time='2024-01-17 10:00:00',
    total_amount=200.00 
WHERE id=1;

INSERT INTO payments (order_id, amount, pay_type) 
VALUES (1, 100.00, 'Final');

更新车辆：status 1→0，store_id 1→2 ⭐ 关键步骤
UPDATE vehicles 
SET status=0,
    store_id=2 
WHERE id=8;

最终数据状态：
┌─────────────────────────────────────────────────────────┐
│ vehicles 表：                                            │
│   id=8  │  status=0 (空闲)        │                     │
│         │  store_id=2 (天津) ⭐   │                     │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ orders 表：                                              │
│   order_no='ORD20240115001'  │  status=2 (已还车)      │
│   actual_return_time='2024-01-17 10:00:00'             │
│   total_amount=200.00                                  │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ payments 表：                                            │
│   order_id=1  │  amount=100  │  pay_type='Deposit'    │
│   order_id=1  │  amount=100  │  pay_type='Final'      │
└─────────────────────────────────────────────────────────┘
```

---

## 关键业务规则

### 1. 车辆生命周期

```
┌──────────┐     ┌────────┐     ┌────────┐     ┌──────────┐
│   空闲   │────▶│  已租  │────▶│ 还车中 │────▶│   空闲   │
│ status=0 │     │status=1│     │status=0│     │status=0  │
└──────────┘     └────────┘     └────────┘     └──────────┘
      △                                               │
      │          ┌──────────┐                        │
      └──────────│   维修   │◀───────────────────────┘
                 │ status=2 │
                 └──────────┘

核心规则：
- 只有status=0的车可以被出租
- 返回车辆时，必须更新store_id为还车门店
- 如果需要维修，将status改为2
- 维修完成后，恢复status为0
```

### 2. 订单生命周期

```
┌────────┐      ┌────────┐      ┌────────┐      ┌────────┐
│  预订  │─────▶│ 使用中 │─────▶│ 已还车 │      │已取消  │
│status=0│      │status=1│      │status=2│      │status=3│
└────────┘      └────────┘      └────────┘      └────────┘
  △                                                  △
  │                                                  │
  └──────────────────────────────────────────────────┘
                     可随时取消
```

### 3. 支付流程

```
┌────────────┐      ┌──────────┐      ┌──────────┐
│   押金     │      │  尾款    │      │  罚金    │
│ Deposit   │      │  Final   │      │ Penalty  │
│ (订单创建) │      │(还车时)  │      │(逾期时)  │
└────────────┘      └──────────┘      └──────────┘

payments 表可以有多条记录，pay_type用于区分
total_amount = SUM(payments.amount)
```

---

## 异地还车的设计精妙之处

### 问题
如果一个租车系统只有"取车门店"，当用户异地还车时，如何知道车现在在哪个门店？

### 解决方案
```
vehicles 表的 store_id：实时记录车的当前位置
orders 表的 pickup_store_id：记录取车点（历史数据）
orders 表的 return_store_id：记录还车点（历史数据）

流程：
1. 取车前：vehicle.store_id = 北京(1)
2. 取车时：vehicle.status = 1
3. 还车时：
   - orders.status = 2
   - orders.actual_return_time = NOW()
   - vehicle.status = 0
   - vehicle.store_id = 天津(2) ⭐

优势：
- 不需要物流表也能追踪车的位置
- 下一个租户可以看到车现在在哪个门店
- 支持链式租车（A→B→C门店）
- 简洁而优雅！
```

---

## 复杂查询示例

### 1. 查询某用户的完整租车历史

```sql
SELECT 
  o.order_no,
  v.plate_number,
  c.name as 车型,
  s1.name as 取车店,
  s2.name as 还车店,
  o.start_time,
  o.actual_return_time,
  CASE o.status
    WHEN 0 THEN '预订'
    WHEN 1 THEN '使用中'
    WHEN 2 THEN '已完成'
    WHEN 3 THEN '已取消'
  END as 订单状态,
  (SELECT SUM(amount) FROM payments WHERE order_id = o.id) as 总支付,
  CURRENT_DATE - o.start_date::date as 实际租期天数
FROM orders o
JOIN vehicles v ON o.vehicle_id = v.id
JOIN categories c ON v.category_id = c.id
JOIN stores s1 ON o.pickup_store_id = s1.id
JOIN stores s2 ON o.return_store_id = s2.id
WHERE o.user_id = 2
ORDER BY o.created_at DESC;
```

### 2. 统计各门店的实时车辆情况

```sql
SELECT 
  s.name as 门店名,
  COUNT(*) as 总车数,
  SUM(CASE WHEN v.status = 0 THEN 1 ELSE 0 END) as 空闲车,
  SUM(CASE WHEN v.status = 1 THEN 1 ELSE 0 END) as 已租车,
  SUM(CASE WHEN v.status = 2 THEN 1 ELSE 0 END) as 维修车,
  SUM(CASE WHEN v.status = 3 THEN 1 ELSE 0 END) as 调拨车
FROM stores s
LEFT JOIN vehicles v ON s.id = v.store_id
GROUP BY s.id, s.name
ORDER BY s.id;
```

### 3. 发现今天应该还但未还的订单

```sql
SELECT 
  o.order_no,
  u.username,
  v.plate_number,
  o.end_time,
  CURRENT_TIMESTAMP - o.end_time as 逾期时长
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN vehicles v ON o.vehicle_id = v.id
WHERE o.status = 1  -- 使用中
AND o.end_time < CURRENT_TIMESTAMP
ORDER BY o.end_time ASC;
```

---

本文档描述了整个数据库架构和业务流程。
理解这些关系对于正确开发业务逻辑至关重要！

