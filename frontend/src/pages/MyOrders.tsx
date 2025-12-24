import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { orderApi } from '../api/order'
import { storeApi } from '../api/store'
import type { Order, Store } from '../types'
import dayjs from 'dayjs'
import './MyOrders.css'

const statusMap: Record<number, { label: string; class: string }> = {
  0: { label: '进行中', class: 'badge-info' },
  1: { label: '已完成', class: 'badge-success' },
  2: { label: '已归还', class: 'badge-success' },
  3: { label: '已取消', class: 'badge-secondary' },
}

export default function MyOrders() {
  const { user } = useAuth()
  const [orders, setOrders] = useState<Order[]>([])
  const [stores, setStores] = useState<Store[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (user) loadOrders()
  }, [user])

  const loadOrders = async () => {
    try {
      const [ordersRes, storesRes] = await Promise.all([
        orderApi.getMyOrders(user!.id),
        storeApi.getAll(),
      ])
      setOrders(ordersRes.data)
      setStores(storesRes.data)
    } catch (error) {
      console.error('加载订单失败', error)
    } finally {
      setLoading(false)
    }
  }

  const getStoreName = (storeId: number) => {
    return stores.find((s) => s.id === storeId)?.name || '-'
  }

  const handleCancel = async (orderId: number) => {
    if (!confirm('确定要取消此订单吗？')) return
    try {
      await orderApi.cancel(orderId)
      loadOrders()
    } catch (error) {
      console.error('取消订单失败', error)
      alert('取消失败，请稍后重试')
    }
  }

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
      </div>
    )
  }

  return (
    <div className="my-orders-page">
      <div className="page-header">
        <h1 className="page-title">我的订单</h1>
      </div>

      {orders.length === 0 ? (
        <div className="empty-state card">
          <div className="empty-state-icon">📋</div>
          <p>暂无订单记录</p>
          <Link to="/vehicles" className="btn btn-primary mt-4">
            去租车
          </Link>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map((order) => (
            <div key={order.id} className="order-card card">
              <div className="order-header">
                <div>
                  <span className="order-no">订单号：{order.orderNo}</span>
                  <span className={`badge ${statusMap[order.status]?.class}`}>
                    {statusMap[order.status]?.label}
                  </span>
                </div>
                <span className="order-time">
                  {dayjs(order.createdAt).format('YYYY-MM-DD HH:mm')}
                </span>
              </div>
              <div className="order-body">
                <div className="order-vehicle">
                  <span className="vehicle-icon">🚗</span>
                  <div>
                    <h4>{order.vehicle?.brand} {order.vehicle?.model}</h4>
                    <p className="text-muted text-sm">
                      {getStoreName(order.pickupStoreId)} → {getStoreName(order.returnStoreId)}
                    </p>
                  </div>
                </div>
                <div className="order-details">
                  <div className="detail-item">
                    <span className="detail-label">取车时间</span>
                    <span>{dayjs(order.startTime).format('YYYY-MM-DD HH:mm')}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">还车时间</span>
                    <span>{dayjs(order.endTime).format('YYYY-MM-DD HH:mm')}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">订单金额</span>
                    <span className="amount">¥{order.totalAmount?.toFixed(2)}</span>
                  </div>
                </div>
              </div>
              <div className="order-footer">
                <Link to={`/orders/${order.id}`} className="btn btn-outline btn-sm">
                  查看详情
                </Link>
                {order.status === 0 && (
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => handleCancel(order.id)}
                  >
                    取消订单
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
