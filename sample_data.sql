-- 车辆租贷管理系统 - 示例数据插入脚本
-- 这个脚本包含大量示例数据，用于测试和演示

-- ============================================
-- 插入测试车辆
-- ============================================

-- 插入经济型车辆 (category_id = 1)
INSERT INTO vehicles (plate_number, model, category_id, store_id, status, daily_rate) VALUES
('京A12345', '大众捷达', 1, 1, 0, 100.00),
('京A12346', '日产阳光', 1, 1, 0, 100.00),
('京A12347', '别克英朗', 1, 2, 0, 100.00),
('京A12348', '现代伊兰特', 1, 3, 0, 100.00),
('京A12349', '哈弗H6', 1, 2, 0, 100.00)
ON CONFLICT (plate_number) DO NOTHING;

-- 插入舒适型车辆 (category_id = 2)
INSERT INTO vehicles (plate_number, model, category_id, store_id, status, daily_rate) VALUES
('京A23456', '丰田卡罗拉', 2, 1, 0, 150.00),
('京A23457', '本田雅阁', 2, 1, 1, 150.00),
('京A23458', '标致408', 2, 2, 0, 150.00),
('京A23459', '吉利博瑞', 2, 3, 0, 150.00),
('京A23460', '荣威i6', 2, 2, 0, 150.00)
ON CONFLICT (plate_number) DO NOTHING;

-- 插入豪华型车辆 (category_id = 3)
INSERT INTO vehicles (plate_number, model, category_id, store_id, status, daily_rate) VALUES
('京A34567', '宝马3系', 3, 1, 0, 250.00),
('京A34568', 'Model 3', 3, 1, 1, 250.00),
('京A34569', '奔驰C级', 3, 2, 0, 250.00),
('京A34570', '奥迪A4', 3, 3, 0, 250.00),
('京A34571', '路虎揽胜', 3, 2, 2, 250.00)
ON CONFLICT (plate_number) DO NOTHING;

-- ============================================
-- 插入测试订单
-- ============================================

-- 生成订单流水号函数（可选）
-- 订单格式: ORD20240101001

-- 插入预订订单
INSERT INTO orders (order_no, user_id, vehicle_id, pickup_store_id, return_store_id, start_time, end_time, status) VALUES
('ORD20240101001', 2, 1, 1, 1, '2024-01-15 10:00:00', '2024-01-17 10:00:00', 0),
('ORD20240101002', 3, 6, 1, 2, '2024-01-20 09:00:00', '2024-01-22 09:00:00', 0)
ON CONFLICT (order_no) DO NOTHING;

-- 插入使用中的订单
INSERT INTO orders (order_no, user_id, vehicle_id, pickup_store_id, return_store_id, start_time, end_time, status) VALUES
('ORD20240102001', 2, 2, 1, 1, '2024-01-10 14:00:00', '2024-01-12 14:00:00', 1),
('ORD20240102002', 3, 8, 1, 2, '2024-01-08 09:00:00', '2024-01-10 09:00:00', 1)
ON CONFLICT (order_no) DO NOTHING;

-- 插入已完成的订单
INSERT INTO orders (order_no, user_id, vehicle_id, pickup_store_id, return_store_id, start_time, end_time, actual_return_time, total_amount, status) VALUES
('ORD20240103001', 2, 5, 1, 1, '2024-01-01 10:00:00', '2024-01-03 10:00:00', '2024-01-03 10:30:00', 200.00, 2),
('ORD20240103002', 3, 9, 2, 3, '2024-01-02 09:00:00', '2024-01-05 09:00:00', '2024-01-05 08:45:00', 450.00, 2),
('ORD20240103003', 2, 11, 1, 1, '2024-01-05 15:00:00', '2024-01-07 15:00:00', '2024-01-07 15:00:00', 500.00, 2)
ON CONFLICT (order_no) DO NOTHING;

-- ============================================
-- 插入支付记录
-- ============================================

-- 订单1的支付记录
INSERT INTO payments (order_id, amount, pay_method, pay_type) VALUES
(1, 100.00, 'Alipay', 'Deposit'),
(1, 100.00, 'Alipay', 'Final')
ON CONFLICT DO NOTHING;

-- 订单2的支付记录
INSERT INTO payments (order_id, amount, pay_method, pay_type) VALUES
(2, 150.00, 'WeChat', 'Deposit'),
(2, 150.00, 'WeChat', 'Final')
ON CONFLICT DO NOTHING;

-- 订单4的支付记录
INSERT INTO payments (order_id, amount, pay_method, pay_type) VALUES
(4, 200.00, 'Card', 'Deposit'),
(4, 200.00, 'Card', 'Final')
ON CONFLICT DO NOTHING;

-- 订单5的支付记录
INSERT INTO payments (order_id, amount, pay_method, pay_type) VALUES
(5, 225.00, 'Alipay', 'Deposit'),
(5, 225.00, 'Alipay', 'Final')
ON CONFLICT DO NOTHING;

-- 订单6的支付记录
INSERT INTO payments (order_id, amount, pay_method, pay_type) VALUES
(6, 250.00, 'WeChat', 'Deposit'),
(6, 250.00, 'WeChat', 'Final')
ON CONFLICT DO NOTHING;

-- ============================================
-- 插入维修记录
-- ============================================

-- 车辆 5 的维修记录
INSERT INTO maintenance (vehicle_id, type, start_date, end_date, cost, description) VALUES
(5, '维修', '2024-01-10', '2024-01-12', 500.00, '更换轮胎和刹车盘'),
(5, '保养', '2024-01-01', '2024-01-01', 200.00, '定期保养，更换机油')
ON CONFLICT DO NOTHING;

-- 车辆 15 的维修记录
INSERT INTO maintenance (vehicle_id, type, start_date, end_date, cost, description) VALUES
(15, '维修', '2024-01-05', '2024-01-08', 1500.00, '发动机故障维修'),
(15, '年检', '2023-12-25', '2023-12-25', 300.00, '年度检验通过')
ON CONFLICT DO NOTHING;

-- 车辆 8 的维修记录
INSERT INTO maintenance (vehicle_id, type, start_date, end_date, cost, description) VALUES
(8, '保养', '2024-01-12', '2024-01-12', 300.00, '定期保养')
ON CONFLICT DO NOTHING;

-- ============================================
-- 显示插入结果统计
-- ============================================

SELECT '数据插入完成，统计如下：' as message;
SELECT 
  (SELECT COUNT(*) FROM vehicles) as vehicle_count,
  (SELECT COUNT(*) FROM orders) as order_count,
  (SELECT COUNT(*) FROM payments) as payment_count,
  (SELECT COUNT(*) FROM maintenance) as maintenance_count;

-- ============================================
-- 验证数据完整性
-- ============================================

-- 查看所有订单详情
SELECT 
  o.id,
  o.order_no,
  u.username,
  v.plate_number,
  s1.name as pickup_store,
  s2.name as return_store,
  o.start_time,
  o.end_time,
  CASE o.status
    WHEN 0 THEN '预订'
    WHEN 1 THEN '使用中'
    WHEN 2 THEN '已还车'
    WHEN 3 THEN '已取消'
  END as status_desc
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN vehicles v ON o.vehicle_id = v.id
JOIN stores s1 ON o.pickup_store_id = s1.id
JOIN stores s2 ON o.return_store_id = s2.id
ORDER BY o.id;

-- 查看所有车辆详情
SELECT 
  v.id,
  v.plate_number,
  v.model,
  c.name as category,
  s.name as store,
  CASE v.status
    WHEN 0 THEN '空闲'
    WHEN 1 THEN '已租'
    WHEN 2 THEN '维修'
    WHEN 3 THEN '调拨中'
  END as status_desc,
  v.daily_rate
FROM vehicles v
JOIN categories c ON v.category_id = c.id
JOIN stores s ON v.store_id = s.id
ORDER BY v.id;
