import api from './client'
import type { Order, OrderDTO } from '../types'

export const orderApi = {
  // 创建订单
  create: (data: OrderDTO) => api.post<Order>('/orders', data),

  // 获取我的订单
  getMyOrders: (userId: number) => 
    api.get<Order[]>('/orders/my', { params: { userId } }),

  // 获取所有订单 (管理员)
  getAll: () => api.get<Order[]>('/orders/all'),

  // 获取单个订单
  getById: (id: number) => api.get<Order>(`/orders/${id}`),

  // 按订单号查询
  getByOrderNo: (orderNo: string) => api.get<Order>(`/orders/no/${orderNo}`),

  // 归还车辆
  returnVehicle: (id: number, storeId: number) =>
    api.post<Order>(`/orders/${id}/return`, null, { params: { storeId } }),

  // 取消订单
  cancel: (id: number) => api.post<Order>(`/orders/${id}/cancel`),
}
