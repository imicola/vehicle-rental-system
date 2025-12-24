import api from './client'
import type { Payment } from '../types'

export const paymentApi = {
  // 支付押金
  payDeposit: (orderId: number) =>
    api.post<Payment>('/payments/deposit', null, { params: { orderId } }),

  // 支付尾款
  payFinal: (orderId: number, amount: number) =>
    api.post<Payment>('/payments/final', null, { params: { orderId, amount } }),

  // 支付罚金
  payPenalty: (orderId: number, amount: number) =>
    api.post<Payment>('/payments/penalty', null, { params: { orderId, amount } }),

  // 获取订单支付记录
  getByOrder: (orderId: number) => api.get<Payment[]>(`/payments/order/${orderId}`),

  // 获取所有支付记录 (管理员)
  getAll: () => api.get<Payment[]>('/payments/all'),
}
