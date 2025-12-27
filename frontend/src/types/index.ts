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
