-- 车辆租贷管理系统数据库脚本 (PostgreSQL)
-- 共7个核心表

-- ============================================
-- 1. 基础信息模块
-- ============================================

-- 1.1 门店表 (stores)
CREATE TABLE IF NOT EXISTS stores (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 1.2 分类表 (categories)
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    basic_rate DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 1.3 用户表 (users)
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    role INT DEFAULT 0,  -- 0:客户, 1:管理员, 2:门店员工
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 2. 核心资产模块
-- ============================================

-- 2.1 车辆表 (vehicles)
CREATE TABLE IF NOT EXISTS vehicles (
    id SERIAL PRIMARY KEY,
    plate_number VARCHAR(20) UNIQUE NOT NULL,
    model VARCHAR(50),
    category_id INT NOT NULL,
    store_id INT NOT NULL,
    status INT DEFAULT 0,  -- 0:空闲, 1:已租, 2:维修, 3:调拨中
    daily_rate DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    CONSTRAINT fk_vehicle_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE RESTRICT
);

-- 2.2 维修记录表 (maintenance)
CREATE TABLE IF NOT EXISTS maintenance (
    id SERIAL PRIMARY KEY,
    vehicle_id INT NOT NULL,
    type VARCHAR(20),  -- 维修/保养/年检
    start_date DATE,
    end_date DATE,
    cost DECIMAL(10, 2),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_maintenance_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

-- ============================================
-- 3. 业务交易模块
-- ============================================

-- 3.1 订单表 (orders)
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    order_no VARCHAR(64) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    pickup_store_id INT NOT NULL,
    return_store_id INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    actual_return_time TIMESTAMP,
    total_amount DECIMAL(10, 2),
    status INT DEFAULT 0,  -- 0:预订, 1:使用中, 2:已还车, 3:已取消
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_order_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE RESTRICT,
    CONSTRAINT fk_order_pickup_store FOREIGN KEY (pickup_store_id) REFERENCES stores(id) ON DELETE RESTRICT,
    CONSTRAINT fk_order_return_store FOREIGN KEY (return_store_id) REFERENCES stores(id) ON DELETE RESTRICT
);

-- 3.2 支付记录表 (payments)
CREATE TABLE IF NOT EXISTS payments (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    pay_method VARCHAR(20),  -- Alipay/WeChat/Card
    pay_type VARCHAR(20),    -- Deposit:押金, Final:尾款, Penalty:罚金
    pay_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- ============================================
-- 4. 索引优化
-- ============================================

-- 车辆表索引
CREATE INDEX IF NOT EXISTS idx_vehicle_category ON vehicles(category_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_store ON vehicles(store_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_status ON vehicles(status);

-- 订单表索引
CREATE INDEX IF NOT EXISTS idx_order_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_order_vehicle ON orders(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_order_start_time ON orders(start_time);

-- 维修记录表索引
CREATE INDEX IF NOT EXISTS idx_maintenance_vehicle ON maintenance(vehicle_id);

-- 支付记录表索引
CREATE INDEX IF NOT EXISTS idx_payment_order ON payments(order_id);

-- ============================================
-- 5. 初始化示例数据（可选）
-- ============================================

-- 插入示例门店
INSERT INTO stores (name, address, phone) VALUES
('北京朝阳店', '北京市朝阳区建国路1号', '010-12345678'),
('天津河西店', '天津市河西区解放南路100号', '022-87654321'),
('上海浦东店', '上海市浦东新区世纪大道888号', '021-66666666')
ON CONFLICT DO NOTHING;

-- 插入示例分类
INSERT INTO categories (name, basic_rate) VALUES
('经济型', 100.00),
('舒适型', 150.00),
('豪华型', 250.00)
ON CONFLICT DO NOTHING;

-- 插入示例用户
INSERT INTO users (username, password, phone, role) VALUES
('admin', '$2a$10$CNOhc9tOHBvpQ.65y93pVuXVktqzUp8QHgkOamfoZxc/TwjY34N0u', '13900000001', 1),
('user001', '$2a$10$YfFTdOvlqbPJIlIcBTFEwuf0evb595A8nwf0b6e2lL9O65mDoZ5qq', '13900000002', 0),
('user002', '$2a$10$YfFTdOvlqbPJIlIcBTFEwuf0evb595A8nwf0b6e2lL9O65mDoZ5qq', '13900000003', 0),
('store_staff', '$2a$10$QYWnndrs8chX5nB.WW6FS.5MpEHCyCKqE7VKJPlkBoVQPgSnQUGtO', '13900000004', 2)
ON CONFLICT DO NOTHING;
