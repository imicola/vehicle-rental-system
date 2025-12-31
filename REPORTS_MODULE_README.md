# 车辆数据分析与报表模块

## 📊 功能概述

本模块为车辆租赁管理系统的管理员提供全面的数据分析和统计报表功能，帮助管理者进行数据驱动的运营决策。

## 🎯 核心功能

### 1. 综合仪表盘 (`/admin/reports - Dashboard`)
展示系统整体运营状况的关键指标：
- **财务指标**：总收入、维修成本、净利润、收入增长率
- **订单指标**：总订单数、完成订单数、订单增长率
- **车辆指标**：总车辆数、各状态车辆分布、平均利用率
- **分布统计**：按分类/门店的车辆分布、按门店的收入分布、订单状态分布

### 2. 收入统计 (`Revenue Statistics`)
按时间周期（日/周/月/年）统计财务收入：
- 总收入、押金、尾款、罚金明细
- 订单数量、完成订单、取消订单
- 平均订单金额

### 3. 车辆利用率 (`Vehicle Utilization`)
分析每辆车的使用效率：
- 订单数量、租赁天数
- 利用率计算（租赁天数/总天数 × 100%）
- 总收入、当前状态
- 按利用率降序排列，快速识别高效/低效车辆

### 4. 维修成本分析 (`Maintenance Cost`)
统计车辆维护支出：
- 维修次数（维修/保养/年检分类统计）
- 总维修成本、平均维修成本
- 净收入计算（车辆收入 - 维修成本）
- 识别高成本车辆，评估ROI

### 5. 订单趋势 (`Order Trend`)
按时间周期分析订单变化趋势：
- 订单总数、各状态订单分布
- 订单总金额
- 完成率、取消率

### 6. 门店分析 (`Store Revenue`)
对比各门店的经营表现：
- 车辆数、订单数
- 总收入、维修成本、净利润
- 平均车辆利用率
- 按收入降序排列，识别优质门店

## 🚀 使用方法

### 后端 API

所有报表API都在 `/api/reports` 路径下，主要接口包括：

```
GET /api/reports/dashboard?startDate={start}&endDate={end}
GET /api/reports/revenue?period={MONTH}&startDate={start}&endDate={end}
GET /api/reports/vehicle-utilization?startDate={start}&endDate={end}
GET /api/reports/maintenance-cost?startDate={start}&endDate={end}
GET /api/reports/order-trend?period={MONTH}&startDate={start}&endDate={end}
GET /api/reports/store-revenue?startDate={start}&endDate={end}
```

**参数说明：**
- `period`: 统计周期，可选值：`DAY`, `WEEK`, `MONTH`, `YEAR`
- `startDate`: 开始日期时间（ISO 8601格式），如 `2025-01-01T00:00:00`
- `endDate`: 结束日期时间（ISO 8601格式），如 `2025-12-31T23:59:59`

**权限要求：** 仅管理员（ADMIN角色）可访问（当前已注释权限校验，可根据需要启用）

### 前端界面

1. 以管理员身份登录系统
2. 进入管理后台 `/admin`
3. 点击"📊 数据报表"卡片
4. 选择时间范围和统计周期
5. 切换不同的报表标签查看数据

**界面特性：**
- 响应式设计，支持移动端访问
- 时间筛选器：灵活选择分析时间范围
- 周期选择器：支持日/周/月/年不同粒度
- 数据表格：清晰展示详细统计数据
- 一键刷新：实时更新最新数据

## 📁 文件结构

### 后端代码

```
src/main/java/com/java_db/demo/
├── controller/
│   └── ReportController.java          # 报表控制器，提供RESTful API
├── service/
│   ├── ReportService.java             # 报表服务接口
│   └── ReportServiceImpl.java         # 报表服务实现，核心业务逻辑
├── dto/
│   ├── ReportPeriod.java              # 时间周期枚举
│   ├── DashboardDTO.java              # 仪表盘数据DTO
│   ├── RevenueStatisticsDTO.java      # 收入统计DTO
│   ├── VehicleUtilizationDTO.java     # 车辆利用率DTO
│   ├── MaintenanceCostDTO.java        # 维修成本DTO
│   ├── OrderTrendDTO.java             # 订单趋势DTO
│   └── StoreRevenueDTO.java           # 门店收入DTO
└── repository/
    ├── OrderRepository.java           # 订单Repository（新增统计方法）
    ├── VehicleRepository.java         # 车辆Repository（新增统计方法）
    ├── PaymentRepository.java         # 支付Repository（新增统计方法）
    └── MaintenanceRepository.java     # 维修Repository（新增统计方法）
```

### 前端代码

```
frontend/src/
├── pages/admin/
│   ├── Reports.tsx                    # 报表主页面组件
│   └── Reports.css                    # 报表样式
├── api/
│   └── report.ts                      # 报表API客户端
└── types/
    └── index.ts                       # 类型定义（新增报表相关接口）
```

## 🔧 技术实现

### 后端技术栈
- **Spring Data JPA**: 使用`@Query`注解编写聚合查询
- **Stream API**: 数据过滤、分组、统计
- **BigDecimal处理**: 精确的金额计算
- **事务管理**: `@Transactional(readOnly = true)` 优化查询性能

### 前端技术栈
- **React Hooks**: `useState`、`useEffect` 状态管理
- **TypeScript**: 类型安全的数据结构
- **Axios**: HTTP请求封装
- **Day.js**: 日期时间处理
- **CSS Grid/Flexbox**: 响应式布局

### 数据计算逻辑

**车辆利用率计算公式：**
```
利用率 = (租赁总天数 / 统计周期天数) × 100%
```

**订单完成率计算公式：**
```
完成率 = (已完成订单数 / 总订单数) × 100%
```

**净利润计算公式：**
```
净利润 = 总收入 - 维修成本
```

## 📈 性能优化建议

1. **数据库索引**: 已在关键字段上创建索引（如`created_at`、`start_time`等）
2. **缓存策略**: 建议为报表数据添加Redis缓存（5-15分钟过期），减少数据库查询
3. **分页加载**: 对于大量数据，可实现分页或虚拟滚动
4. **异步查询**: 耗时查询可改为异步任务，前端轮询结果

## 🎨 界面预览

### 综合仪表盘
- 6个关键指标卡片（总收入、订单数、利用率等）
- 4个数据分布面板（车辆状态、订单状态、分类分布、门店收入）

### 数据表格
- 表头固定、支持横向滚动
- 金额右对齐、百分比高亮显示
- 状态徽章（不同颜色区分状态）
- 悬停高亮行

## 🔐 安全说明

- **权限控制**: ReportController使用`@PreAuthorize`注解限制仅管理员访问（当前已注释，需启用Spring Security）
- **数据权限**: 建议添加数据权限控制，如门店经理只能查看自己门店的数据
- **SQL注入防护**: 使用参数化查询，避免SQL注入风险

## 🚧 未来扩展

1. **数据可视化**: 集成ECharts/Recharts，添加图表展示（折线图、柱状图、饼图）
2. **导出功能**: 支持导出Excel/PDF报表
3. **自定义报表**: 允许用户自定义筛选条件和展示字段
4. **数据对比**: 支持多时间段对比（环比、同比）
5. **预警功能**: 设置阈值，低利用率车辆/高成本车辆自动预警
6. **定时报表**: 定时生成报表并发送邮件

## 📝 测试建议

1. **单元测试**: 测试Service层的统计计算逻辑
2. **集成测试**: 测试Repository的聚合查询
3. **性能测试**: 模拟大数据量场景，测试查询性能
4. **边界测试**: 测试空数据、单条数据、跨年度查询等边界情况

## 🐛 已知问题

1. 当前权限注解已注释，需根据项目实际的Security配置启用
2. 大数据量场景下可能存在性能问题，建议添加缓存
3. 前端暂无图表可视化，仅表格展示

---

**作者**: AI Assistant  
**创建日期**: 2025-12-31  
**版本**: v1.0.0
