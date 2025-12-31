import { useState, useEffect } from 'react'
import { reportApi } from '../../api/report'
import type { 
  DashboardData, 
  RevenueStatistics, 
  VehicleUtilization, 
  MaintenanceCost,
  OrderTrend,
  StoreRevenue,
  ReportPeriod 
} from '../../types'
import dayjs from 'dayjs'
import './Reports.css'

type ReportTab = 'dashboard' | 'revenue' | 'vehicle' | 'maintenance' | 'order' | 'store'

const Reports = () => {
  const [activeTab, setActiveTab] = useState<ReportTab>('dashboard')
  const [loading, setLoading] = useState(false)
  const [period, setPeriod] = useState<ReportPeriod>('MONTH')
  
  // 默认时间范围：最近30天
  const [startDate, setStartDate] = useState(dayjs().subtract(30, 'days').format('YYYY-MM-DD'))
  const [endDate, setEndDate] = useState(dayjs().format('YYYY-MM-DD'))
  
  // 数据状态
  const [dashboard, setDashboard] = useState<DashboardData | null>(null)
  const [revenueStats, setRevenueStats] = useState<RevenueStatistics[]>([])
  const [vehicleUtil, setVehicleUtil] = useState<VehicleUtilization[]>([])
  const [maintenanceCost, setMaintenanceCost] = useState<MaintenanceCost[]>([])
  const [orderTrend, setOrderTrend] = useState<OrderTrend[]>([])
  const [storeRevenue, setStoreRevenue] = useState<StoreRevenue[]>([])

  // 加载数据
  const loadData = async () => {
    setLoading(true)
    try {
      const start = dayjs(startDate).startOf('day').format('YYYY-MM-DDTHH:mm:ss')
      const end = dayjs(endDate).endOf('day').format('YYYY-MM-DDTHH:mm:ss')

      switch (activeTab) {
        case 'dashboard':
          const dashboardRes = await reportApi.getDashboard({ startDate: start, endDate: end })
          setDashboard(dashboardRes.data)
          break
        case 'revenue':
          const revenueRes = await reportApi.getRevenueStatistics({ period, startDate: start, endDate: end })
          setRevenueStats(revenueRes.data)
          break
        case 'vehicle':
          const vehicleRes = await reportApi.getVehicleUtilization({ startDate: start, endDate: end })
          setVehicleUtil(vehicleRes.data)
          break
        case 'maintenance':
          const maintenanceRes = await reportApi.getMaintenanceCost({ startDate, endDate })
          setMaintenanceCost(maintenanceRes.data)
          break
        case 'order':
          const orderRes = await reportApi.getOrderTrend({ period, startDate: start, endDate: end })
          setOrderTrend(orderRes.data)
          break
        case 'store':
          const storeRes = await reportApi.getStoreRevenue({ startDate: start, endDate: end })
          setStoreRevenue(storeRes.data)
          break
      }
    } catch (error) {
      console.error('加载报表数据失败:', error)
      alert('加载报表数据失败，请重试')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [activeTab, startDate, endDate, period])

  // 格式化金额
  const formatCurrency = (amount: number) => `¥${amount.toFixed(2)}`

  // 格式化百分比
  const formatPercent = (value: number) => `${value.toFixed(2)}%`

  // 获取车辆状态文本
  const getVehicleStatusText = (status: number) => {
    const statusMap: Record<number, string> = {
      0: '空闲',
      1: '已租',
      2: '维修中',
      3: '调拨中'
    }
    return statusMap[status] || '未知'
  }

  return (
    <div className="reports-container">
      <h1>数据分析与报表</h1>

      {/* 时间范围选择 */}
      <div className="filters">
        <div className="date-range">
          <label>开始日期:</label>
          <input 
            type="date" 
            value={startDate} 
            onChange={(e) => setStartDate(e.target.value)} 
          />
          <label>结束日期:</label>
          <input 
            type="date" 
            value={endDate} 
            onChange={(e) => setEndDate(e.target.value)} 
          />
        </div>
        
        {(activeTab === 'revenue' || activeTab === 'order') && (
          <div className="period-select">
            <label>统计周期:</label>
            <select value={period} onChange={(e) => setPeriod(e.target.value as ReportPeriod)}>
              <option value="DAY">日</option>
              <option value="WEEK">周</option>
              <option value="MONTH">月</option>
              <option value="YEAR">年</option>
            </select>
          </div>
        )}
        
        <button className="refresh-btn" onClick={loadData} disabled={loading}>
          {loading ? '加载中...' : '刷新数据'}
        </button>
      </div>

      {/* 标签页导航 */}
      <div className="tabs">
        <button 
          className={activeTab === 'dashboard' ? 'active' : ''} 
          onClick={() => setActiveTab('dashboard')}
        >
          📊 综合仪表盘
        </button>
        <button 
          className={activeTab === 'revenue' ? 'active' : ''} 
          onClick={() => setActiveTab('revenue')}
        >
          💰 收入统计
        </button>
        <button 
          className={activeTab === 'vehicle' ? 'active' : ''} 
          onClick={() => setActiveTab('vehicle')}
        >
          🚗 车辆利用率
        </button>
        <button 
          className={activeTab === 'maintenance' ? 'active' : ''} 
          onClick={() => setActiveTab('maintenance')}
        >
          🔧 维修成本
        </button>
        <button 
          className={activeTab === 'order' ? 'active' : ''} 
          onClick={() => setActiveTab('order')}
        >
          📈 订单趋势
        </button>
        <button 
          className={activeTab === 'store' ? 'active' : ''} 
          onClick={() => setActiveTab('store')}
        >
          🏪 门店分析
        </button>
      </div>

      {/* 内容区域 */}
      <div className="report-content">
        {loading && <div className="loading">加载中...</div>}

        {/* 综合仪表盘 */}
        {activeTab === 'dashboard' && dashboard && (
          <div className="dashboard-content">
            <div className="kpi-cards">
              <div className="kpi-card">
                <h3>总收入</h3>
                <p className="value">{formatCurrency(dashboard.totalRevenue)}</p>
                <span className="trend">增长率: {formatPercent(dashboard.revenueGrowthRate)}</span>
              </div>
              <div className="kpi-card">
                <h3>总订单</h3>
                <p className="value">{dashboard.totalOrders}</p>
                <span className="trend">增长率: {formatPercent(dashboard.orderGrowthRate)}</span>
              </div>
              <div className="kpi-card">
                <h3>完成订单</h3>
                <p className="value">{dashboard.completedOrders}</p>
              </div>
              <div className="kpi-card">
                <h3>平均利用率</h3>
                <p className="value">{formatPercent(dashboard.averageUtilizationRate)}</p>
              </div>
              <div className="kpi-card">
                <h3>维修成本</h3>
                <p className="value">{formatCurrency(dashboard.totalMaintenanceCost)}</p>
              </div>
              <div className="kpi-card">
                <h3>净利润</h3>
                <p className="value profit">{formatCurrency(dashboard.netProfit)}</p>
              </div>
            </div>

            <div className="dashboard-grid">
              <div className="dashboard-section">
                <h3>车辆状态分布</h3>
                <div className="stats-list">
                  <div className="stat-item">
                    <span>总车辆数:</span>
                    <strong>{dashboard.totalVehicles}</strong>
                  </div>
                  <div className="stat-item">
                    <span>可用:</span>
                    <strong className="available">{dashboard.availableVehicles}</strong>
                  </div>
                  <div className="stat-item">
                    <span>已租:</span>
                    <strong className="rented">{dashboard.rentedVehicles}</strong>
                  </div>
                  <div className="stat-item">
                    <span>维修中:</span>
                    <strong className="maintenance">{dashboard.maintenanceVehicles}</strong>
                  </div>
                  <div className="stat-item">
                    <span>调拨中:</span>
                    <strong>{dashboard.transferVehicles}</strong>
                  </div>
                </div>
              </div>

              <div className="dashboard-section">
                <h3>订单状态分布</h3>
                <div className="stats-list">
                  {Object.entries(dashboard.orderByStatus).map(([status, count]) => (
                    <div key={status} className="stat-item">
                      <span>{status}:</span>
                      <strong>{count}</strong>
                    </div>
                  ))}
                </div>
              </div>

              <div className="dashboard-section">
                <h3>按分类车辆分布</h3>
                <div className="stats-list">
                  {Object.entries(dashboard.vehicleByCategory).map(([category, count]) => (
                    <div key={category} className="stat-item">
                      <span>{category}:</span>
                      <strong>{count}</strong>
                    </div>
                  ))}
                </div>
              </div>

              <div className="dashboard-section">
                <h3>门店收入分布</h3>
                <div className="stats-list">
                  {Object.entries(dashboard.revenueByStore).map(([store, revenue]) => (
                    <div key={store} className="stat-item">
                      <span>{store}:</span>
                      <strong>{formatCurrency(revenue)}</strong>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* 收入统计 */}
        {activeTab === 'revenue' && (
          <div className="table-container">
            <table className="report-table">
              <thead>
                <tr>
                  <th>时间段</th>
                  <th>总收入</th>
                  <th>押金</th>
                  <th>尾款</th>
                  <th>罚金</th>
                  <th>订单数</th>
                  <th>完成订单</th>
                  <th>取消订单</th>
                  <th>平均金额</th>
                </tr>
              </thead>
              <tbody>
                {revenueStats.map((stat, index) => (
                  <tr key={index}>
                    <td>{stat.period}</td>
                    <td className="currency">{formatCurrency(stat.totalRevenue)}</td>
                    <td>{formatCurrency(stat.depositAmount)}</td>
                    <td>{formatCurrency(stat.finalPaymentAmount)}</td>
                    <td>{formatCurrency(stat.penaltyAmount)}</td>
                    <td>{stat.orderCount}</td>
                    <td>{stat.completedOrderCount}</td>
                    <td>{stat.cancelledOrderCount}</td>
                    <td>{formatCurrency(stat.averageOrderAmount)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* 车辆利用率 */}
        {activeTab === 'vehicle' && (
          <div className="table-container">
            <table className="report-table">
              <thead>
                <tr>
                  <th>车牌号</th>
                  <th>车型</th>
                  <th>分类</th>
                  <th>门店</th>
                  <th>订单数</th>
                  <th>租赁天数</th>
                  <th>利用率</th>
                  <th>总收入</th>
                  <th>状态</th>
                </tr>
              </thead>
              <tbody>
                {vehicleUtil.map((vehicle) => (
                  <tr key={vehicle.vehicleId}>
                    <td>{vehicle.licensePlate}</td>
                    <td>{vehicle.model}</td>
                    <td>{vehicle.categoryName}</td>
                    <td>{vehicle.storeName}</td>
                    <td>{vehicle.totalOrders}</td>
                    <td>{vehicle.totalRentalDays}</td>
                    <td className="percent">{formatPercent(vehicle.utilizationRate)}</td>
                    <td className="currency">{formatCurrency(vehicle.totalRevenue)}</td>
                    <td>
                      <span className={`status-badge status-${vehicle.status}`}>
                        {getVehicleStatusText(vehicle.status)}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* 维修成本 */}
        {activeTab === 'maintenance' && (
          <div className="table-container">
            <table className="report-table">
              <thead>
                <tr>
                  <th>车牌号</th>
                  <th>车型</th>
                  <th>分类</th>
                  <th>维修次数</th>
                  <th>维修</th>
                  <th>保养</th>
                  <th>年检</th>
                  <th>总成本</th>
                  <th>平均成本</th>
                  <th>净收入</th>
                </tr>
              </thead>
              <tbody>
                {maintenanceCost.map((item) => (
                  <tr key={item.vehicleId}>
                    <td>{item.licensePlate}</td>
                    <td>{item.model}</td>
                    <td>{item.categoryName}</td>
                    <td>{item.maintenanceCount}</td>
                    <td>{item.repairCount}</td>
                    <td>{item.serviceCount}</td>
                    <td>{item.inspectionCount}</td>
                    <td className="currency">{formatCurrency(item.totalCost)}</td>
                    <td>{formatCurrency(item.averageCost)}</td>
                    <td className={`currency ${item.revenueMinusCost >= 0 ? 'profit' : 'loss'}`}>
                      {formatCurrency(item.revenueMinusCost)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* 订单趋势 */}
        {activeTab === 'order' && (
          <div className="table-container">
            <table className="report-table">
              <thead>
                <tr>
                  <th>时间段</th>
                  <th>总订单</th>
                  <th>预订中</th>
                  <th>使用中</th>
                  <th>已完成</th>
                  <th>已取消</th>
                  <th>总金额</th>
                  <th>完成率</th>
                  <th>取消率</th>
                </tr>
              </thead>
              <tbody>
                {orderTrend.map((trend, index) => (
                  <tr key={index}>
                    <td>{trend.period}</td>
                    <td>{trend.totalOrders}</td>
                    <td>{trend.pendingOrders}</td>
                    <td>{trend.activeOrders}</td>
                    <td>{trend.completedOrders}</td>
                    <td>{trend.cancelledOrders}</td>
                    <td className="currency">{formatCurrency(trend.totalAmount)}</td>
                    <td className="percent">{formatPercent(trend.completionRate)}</td>
                    <td className="percent">{formatPercent(trend.cancellationRate)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* 门店分析 */}
        {activeTab === 'store' && (
          <div className="table-container">
            <table className="report-table">
              <thead>
                <tr>
                  <th>门店名称</th>
                  <th>地址</th>
                  <th>车辆数</th>
                  <th>订单数</th>
                  <th>总收入</th>
                  <th>维修成本</th>
                  <th>净利润</th>
                  <th>平均利用率</th>
                </tr>
              </thead>
              <tbody>
                {storeRevenue.map((store) => (
                  <tr key={store.storeId}>
                    <td>{store.storeName}</td>
                    <td>{store.address}</td>
                    <td>{store.vehicleCount}</td>
                    <td>{store.orderCount}</td>
                    <td className="currency">{formatCurrency(store.totalRevenue)}</td>
                    <td className="currency">{formatCurrency(store.maintenanceCost)}</td>
                    <td className={`currency ${store.netProfit >= 0 ? 'profit' : 'loss'}`}>
                      {formatCurrency(store.netProfit)}
                    </td>
                    <td className="percent">{formatPercent(store.averageUtilization)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

export default Reports
