-- 车辆租贷管理系统 - 数据库验证脚本
-- 用于验证表创建和初始数据是否正确

-- ============================================
-- 1. 验证所有表是否创建成功
-- ============================================

-- 查看所有表
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

-- ============================================
-- 2. 验证表结构
-- ============================================

-- 门店表
SELECT * FROM stores;

-- 分类表
SELECT * FROM categories;

-- 用户表
SELECT * FROM users;

-- 车辆表（初始为空）
SELECT COUNT(*) as vehicle_count FROM vehicles;

-- 订单表（初始为空）
SELECT COUNT(*) as order_count FROM orders;

-- 支付表（初始为空）
SELECT COUNT(*) as payment_count FROM payments;

-- 维修记录表（初始为空）
SELECT COUNT(*) as maintenance_count FROM maintenance;

-- ============================================
-- 3. 验证外键约束
-- ============================================

-- 查看所有外键
SELECT constraint_name, table_name, column_name, foreign_table_name
FROM information_schema.key_column_usage
WHERE table_schema = 'public' 
AND foreign_table_name IS NOT NULL
ORDER BY table_name;

-- ============================================
-- 4. 验证索引
-- ============================================

-- 查看所有索引
SELECT schemaname, tablename, indexname
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY tablename, indexname;

-- ============================================
-- 5. 查看完整的表结构示例
-- ============================================

-- 门店表结构
\d stores

-- 车辆表结构
\d vehicles

-- 订单表结构
\d orders

-- ============================================
-- 6. 验证示例数据
-- ============================================

-- 统计初始数据
SELECT 
  (SELECT COUNT(*) FROM stores) as store_count,
  (SELECT COUNT(*) FROM categories) as category_count,
  (SELECT COUNT(*) FROM users) as user_count,
  (SELECT COUNT(*) FROM vehicles) as vehicle_count,
  (SELECT COUNT(*) FROM orders) as order_count,
  (SELECT COUNT(*) FROM payments) as payment_count,
  (SELECT COUNT(*) FROM maintenance) as maintenance_count;

-- ============================================
-- 7. 测试查询示例
-- ============================================

-- 显示所有门店及其拥有的车辆数
SELECT 
  s.id,
  s.name,
  s.address,
  COUNT(v.id) as vehicle_count
FROM stores s
LEFT JOIN vehicles v ON s.id = v.store_id
GROUP BY s.id, s.name, s.address
ORDER BY s.id;

-- 显示所有分类及其车辆数
SELECT 
  c.id,
  c.name,
  c.basic_rate,
  COUNT(v.id) as vehicle_count
FROM categories c
LEFT JOIN vehicles v ON c.id = v.category_id
GROUP BY c.id, c.name, c.basic_rate
ORDER BY c.id;

-- 显示所有用户
SELECT 
  id,
  username,
  phone,
  CASE role
    WHEN 0 THEN '客户'
    WHEN 1 THEN '管理员'
    WHEN 2 THEN '门店员工'
    ELSE '未知'
  END as role_desc
FROM users
ORDER BY id;
