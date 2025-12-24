import api from './client'
import type { Vehicle, VehicleDTO, Category } from '../types'

export const vehicleApi = {
  // 搜索可用车辆
  search: (params: { storeId?: number; start?: string; end?: string }) =>
    api.get<Vehicle[]>('/vehicles', { params }),

  // 获取所有车辆
  getAll: () => api.get<Vehicle[]>('/vehicles/all'),

  // 获取单个车辆
  getById: (id: number) => api.get<Vehicle>(`/vehicles/${id}`),

  // 按门店获取车辆
  getByStore: (storeId: number) => api.get<Vehicle[]>(`/vehicles/store/${storeId}`),

  // 创建车辆 (管理员)
  create: (data: VehicleDTO) => api.post<Vehicle>('/vehicles', data),

  // 更新车辆状态 (管理员)
  updateStatus: (id: number, status: number) =>
    api.put<Vehicle>(`/vehicles/${id}/status`, null, { params: { status } }),
}

export const categoryApi = {
  getAll: () => api.get<Category[]>('/categories'),
  getById: (id: number) => api.get<Category>(`/categories/${id}`),
}
