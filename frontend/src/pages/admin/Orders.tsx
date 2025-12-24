import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { orderApi } from '../../api/order'
import { storeApi } from '../../api/store'
import type { Order, Store } from '../../types'
import dayjs from 'dayjs'
import './Admin.css'

const statusMap: Record<number, { label: string; class: string }> = {
  0: { label: '进行中', class: 'badge-info' },
  1: { label: '已完成', class: 'badge-success' },
  2: { label: '已归还', class: 'badge-success' },
  3: { label: '已取消', class: 'badge-secondary' },
}

export default function AdminOrders() {
  const [orders, setOrders] = useState<Order[]>([])
  const [stores, setStores] = useState<Store[]>([])
  const [loading, setLoading] = useState(true)
  const [filterStatus, setFilterStatus] = useState<string>('')

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const [ordersRes, storesRes] = await Promise.all([
        orderApi.getAll(),
        storeApi.getAll(),
      ])
      setOrders(ordersRes.data)
      setStores(storesRes.data)
    } catch (error) {
      console.error('加载数据失败', error)
    } finally {
      setLoading(false)
    }
  }

  const getStoreName = (id: number) => stores.find((s) => s.id === id)?.name || '-'

  const filteredOrders = filterStatus
    ? orders.filter((o) => String(o.status) === filterStatus)
    : orders

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
      </div>
    )
  }

  return (
    <div className="admin-page">
      <div className="page-header">
        <h1 className="page-title">订单管理</h1>
        <Link to="/admin" className="btn btn-outline btn-sm">← 返回</Link>
      </div>

      <div className="admin-toolbar">
        <div className="admin-filters">
          <select
            className="form-select"
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
            style={{ width: 'auto' }}
          >
            <option value="">全部状态</option>
            <option value="0">进行中</option>
            <option value="1">已完成</option>
            <option value="2">已归还</option>
            <option value="3">已取消</option>
          </select>
          <span className="text-muted">共 {filteredOrders.length} 条订单</span>
        </div>
      </div>

      <div className="card admin-table-card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>订单号</th>
                <th>用户</th>
                <th>车辆</th>
                <th>取车门店</th>
                <th>还车门店</th>
                <th>取车时间</th>
                <th>还车时间</th>
                <th>金额</th>
                <th>状态</th>
                <th>创建时间</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.map((order) => (
                <tr key={order.id}>
                  <td>
                    <Link to={`/orders/${order.id}`}>{order.orderNo}</Link>
                  </td>
                  <td>{order.user?.username || order.userId}</td>
                  <td>{order.vehicle?.brand} {order.vehicle?.model}</td>
                  <td>{getStoreName(order.pickupStoreId)}</td>
                  <td>{getStoreName(order.returnStoreId)}</td>
                  <td>{dayjs(order.startTime).format('MM-DD HH:mm')}</td>
                  <td>{dayjs(order.endTime).format('MM-DD HH:mm')}</td>
                  <td>¥{order.totalAmount?.toFixed(2)}</td>
                  <td>
                    <span className={`badge ${statusMap[order.status]?.class}`}>
                      {statusMap[order.status]?.label}
                    </span>
                  </td>
                  <td>{dayjs(order.createdAt).format('MM-DD HH:mm')}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
