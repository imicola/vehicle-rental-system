// 用户
export interface User {
  id: number
  username: string
  phone?: string
  role: 'USER' | 'ADMIN'
  createdAt?: string
}

// 登录请求
export interface LoginRequest {
  username: string
  password: string
}

// 登录响应
export interface LoginResponse {
  token: string
  userId: number
  username: string
  role: 'USER' | 'ADMIN'
  phone?: string
}

// 注册请求
export interface RegisterDTO {
  username: string
  password: string
  phone?: string
}

// 车辆分类
export interface Category {
  id: number
  name: string
  description?: string
}

// 门店
export interface Store {
  id: number
  name: string
  address?: string
  phone?: string
}

// 车辆状态: 0-可用, 1-已租出, 2-维护中, 3-下架
export type VehicleStatus = 0 | 1 | 2 | 3

// 车辆
export interface Vehicle {
  id: number
  brand: string
  model: string
  plateNumber?: string
  categoryId: number
  category?: Category
  storeId: number
  store?: Store
  dailyRate: number
  status: VehicleStatus
  imageUrl?: string
  createdAt?: string
}

// 车辆DTO
export interface VehicleDTO {
  plateNumber: string
  brand: string
  model: string
  categoryId: number
  storeId: number
  dailyRate: number
}

// 订单状态: 0-进行中, 1-已完成, 2-已归还, 3-已取消
export type OrderStatus = 0 | 1 | 2 | 3

// 订单
export interface Order {
  id: number
  orderNo: string
  userId: number
  user?: User
  vehicleId: number
  vehicle?: Vehicle
  pickupStoreId: number
  pickupStore?: Store
  returnStoreId: number
  returnStore?: Store
  startTime: string
  endTime: string
  actualReturnTime?: string
  totalAmount: number
  status: OrderStatus
  createdAt?: string
}

// 订单DTO
export interface OrderDTO {
  userId: number
  vehicleId: number
  pickupStoreId: number
  returnStoreId: number
  startTime: string
  endTime: string
}

// 支付类型
export type PayType = 'Deposit' | 'Final' | 'Penalty'

// 支付记录
export interface Payment {
  id: number
  orderId: number
  order?: Order
  amount: number
  payType: PayType
  payTime: string
}

// 维护记录
export interface Maintenance {
  id: number
  vehicleId: number
  vehicle?: Vehicle
  type: string
  description?: string
  cost: number
  startDate: string
  endDate?: string
}

// 门店DTO
export interface StoreDTO {
  name: string
  address?: string
  phone?: string
}

// ==================== 报表相关类型 ====================

// 报表时间周期
export type ReportPeriod = 'DAY' | 'WEEK' | 'MONTH' | 'YEAR'

// 综合仪表盘数据
export interface DashboardData {
  // 关键指标
  totalRevenue: number
  totalMaintenanceCost: number
  netProfit: number
  totalOrders: number
  completedOrders: number
  averageUtilizationRate: number
  
  // 车辆统计
  totalVehicles: number
  availableVehicles: number
  rentedVehicles: number
  maintenanceVehicles: number
  transferVehicles: number
  
  // 分布统计
  vehicleByCategory: Record<string, number>
  vehicleByStore: Record<string, number>
  orderByStatus: Record<string, number>
  revenueByStore: Record<string, number>
  
  // 趋势数据
  revenueGrowthRate: number
  orderGrowthRate: number
}

// 收入统计
export interface RevenueStatistics {
  period: string
  totalRevenue: number
  depositAmount: number
  finalPaymentAmount: number
  penaltyAmount: number
  orderCount: number
  completedOrderCount: number
  cancelledOrderCount: number
  averageOrderAmount: number
}

// 车辆利用率
export interface VehicleUtilization {
  vehicleId: number
  licensePlate: string
  model: string
  categoryName: string
  storeName: string
  totalOrders: number
  totalRentalDays: number
  utilizationRate: number
  totalRevenue: number
  status: number
}

// 维修成本
export interface MaintenanceCost {
  vehicleId: number
  licensePlate: string
  model: string
  categoryName: string
  maintenanceCount: number
  repairCount: number
  serviceCount: number
  inspectionCount: number
  totalCost: number
  averageCost: number
  revenueMinusCost: number
}

// 订单趋势
export interface OrderTrend {
  period: string
  totalOrders: number
  pendingOrders: number
  activeOrders: number
  completedOrders: number
  cancelledOrders: number
  totalAmount: number
  completionRate: number
  cancellationRate: number
}

// 门店收入统计
export interface StoreRevenue {
  storeId: number
  storeName: string
  address: string
  vehicleCount: number
  orderCount: number
  totalRevenue: number
  maintenanceCost: number
  netProfit: number
  averageUtilization: number
}
