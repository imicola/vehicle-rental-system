import api from './client'
import type { Store, StoreDTO } from '../types'

export const storeApi = {
  getAll: () => api.get<Store[]>('/stores'),
  getById: (id: number) => api.get<Store>(`/stores/${id}`),
  create: (data: StoreDTO) => api.post<Store>('/stores', data),
  update: (id: number, data: StoreDTO) => api.put<Store>(`/stores/${id}`, data),
}
