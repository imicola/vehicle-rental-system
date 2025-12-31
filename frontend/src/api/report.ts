import api from './client'
import type { 
  DashboardData, 
  RevenueStatistics, 
  VehicleUtilization, 
  MaintenanceCost, 
  OrderTrend, 
  StoreRevenue,
  ReportPeriod 
} from '../types'

export const reportApi = {
  // 获取综合仪表盘
  getDashboard: (params: { startDate: string; endDate: string }) =>
    api.get<DashboardData>('/reports/dashboard', { params }),

  // 获取收入统计
  getRevenueStatistics: (params: { 
    period: ReportPeriod; 
    startDate: string; 
    endDate: string 
  }) =>
    api.get<RevenueStatistics[]>('/reports/revenue', { params }),

  // 获取车辆利用率
  getVehicleUtilization: (params: { startDate: string; endDate: string }) =>
    api.get<VehicleUtilization[]>('/reports/vehicle-utilization', { params }),

  // 获取维修成本分析
  getMaintenanceCost: (params: { startDate: string; endDate: string }) =>
    api.get<MaintenanceCost[]>('/reports/maintenance-cost', { params }),

  // 获取订单趋势
  getOrderTrend: (params: { 
    period: ReportPeriod; 
    startDate: string; 
    endDate: string 
  }) =>
    api.get<OrderTrend[]>('/reports/order-trend', { params }),

  // 获取门店收入统计
  getStoreRevenue: (params: { startDate: string; endDate: string }) =>
    api.get<StoreRevenue[]>('/reports/store-revenue', { params }),
}
