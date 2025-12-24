import api from './client'
import type { Maintenance } from '../types'

export const maintenanceApi = {
  // 创建维护记录
  create: (params: {
    vehicleId: number
    type: string
    startDate: string
    cost: number
    description?: string
  }) => api.post<Maintenance>('/maintenance', null, { params }),

  // 完成维护
  complete: (id: number) => api.put<Maintenance>(`/maintenance/${id}/complete`),

  // 获取车辆维护记录
  getByVehicle: (vehicleId: number) =>
    api.get<Maintenance[]>(`/maintenance/vehicle/${vehicleId}`),

  // 获取所有维护记录 (管理员)
  getAll: () => api.get<Maintenance[]>('/maintenance/all'),
}
