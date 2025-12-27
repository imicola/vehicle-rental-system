import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { orderApi } from '../api/order'
import { paymentApi } from '../api/payment'
import { storeApi } from '../api/store'
import type { Order, Payment, Store } from '../types'
import dayjs from 'dayjs'
import ReturnModal from '../components/ReturnModal'
import './OrderDetail.css'

const orderStatusMap: Record<number, { label: string; class: string }> = {
  0: { label: '进行中', class: 'badge-info' },
  1: { label: '已完成', class: 'badge-success' },
  2: { label: '已归还', class: 'badge-success' },
  3: { label: '已取消', class: 'badge-secondary' },
}

const payTypeMap: Record<string, string> = {
  Deposit: '押金',
  Final: '尾款',
  Penalty: '罚金',
}

export default function OrderDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [order, setOrder] = useState<Order | null>(null)
  const [payments, setPayments] = useState<Payment[]>([])
  const [stores, setStores] = useState<Store[]>([])
  const [loading, setLoading] = useState(true)
  const [showReturnModal, setShowReturnModal] = useState(false)

  useEffect(() => {
    if (id) loadData(Number(id))
  }, [id])

  const loadData = async (orderId: number) => {
    try {
      const [orderRes, paymentsRes, storesRes] = await Promise.all([
        orderApi.getById(orderId),
        paymentApi.getByOrder(orderId),
        storeApi.getAll(),
      ])
      setOrder(orderRes.data)
      setPayments(paymentsRes.data)
      setStores(storesRes.data)
    } catch (error) {
      console.error('加载订单详情失败', error)
      navigate('/orders')
    } finally {
      setLoading(false)
    }
  }

  const getStoreName = (storeId?: number, storeObj?: Store) =>
    storeObj?.name || (storeId ? stores.find((s) => s.id === storeId)?.name : undefined) || '-'

  const handlePayDeposit = async () => {
    if (!order) return
    try {
      await paymentApi.payDeposit(order.id)
      loadData(order.id)
    } catch (error) {
      console.error('支付押金失败', error)
      alert('支付失败，请稍后重试')
    }
  }

  const handlePayFinal = async () => {
    if (!order) return
    const depositPaid = payments.find((p) => p.payType === 'Deposit')
    if (!depositPaid) {
      alert('请先支付押金')
      return
    }
    try {
      const finalAmount = order.totalAmount - (order.vehicle?.dailyRate || 0) * 3
      await paymentApi.payFinal(order.id, Math.max(finalAmount, 0))
      loadData(order.id)
    } catch (error) {
      console.error('支付尾款失败', error)
      alert('支付失败，请稍后重试')
    }
  }

  const handleCancel = async () => {
    if (!order) return
    if (!confirm('确定要取消此订单吗？')) return
    try {
      await orderApi.cancel(order.id)
      loadData(order.id)
    } catch (error) {
      console.error('取消订单失败', error)
      alert('取消失败，请稍后重试')
    }
  }

  if (loading || !order) {
    return (
      <div className="loading">
        <div className="spinner"></div>
      </div>
    )
  }

  const depositPaid = payments.some((p) => p.payType === 'Deposit')
  const finalPaid = payments.some((p) => p.payType === 'Final')
  const depositAmount = (order.vehicle?.dailyRate || 0) * 3

  return (
    <div className="order-detail-page">
      <div className="page-header">
        <Link to="/orders" className="back-link">← 返回订单列表</Link>
      </div>

      <div className="detail-grid">
        <div className="order-info card">
          <div className="card-header">
            <h2>订单信息</h2>
            <span className={`badge ${orderStatusMap[order.status]?.class}`}>
              {orderStatusMap[order.status]?.label}
            </span>
          </div>
          <div className="info-list">
            <div className="info-item">
              <span className="info-label">订单号</span>
              <span className="info-value">{order.orderNo}</span>
            </div>
            <div className="info-item">
              <span className="info-label">车辆</span>
              <span className="info-value">
                🚗 {order.vehicle?.brand} {order.vehicle?.model}
              </span>
            </div>
            <div className="info-item">
              <span className="info-label">取车门店</span>
              <span className="info-value">{getStoreName(order.pickupStoreId, order.pickupStore)}</span>
            </div>
            <div className="info-item">
              <span className="info-label">还车门店</span>
              <span className="info-value">{getStoreName(order.returnStoreId, order.returnStore)}</span>
            </div>
            <div className="info-item">
              <span className="info-label">取车时间</span>
              <span className="info-value">
                {dayjs(order.startTime).format('YYYY-MM-DD HH:mm')}
              </span>
            </div>
            <div className="info-item">
              <span className="info-label">还车时间</span>
              <span className="info-value">
                {dayjs(order.endTime).format('YYYY-MM-DD HH:mm')}
              </span>
            </div>
            {order.actualReturnTime && (
              <div className="info-item">
                <span className="info-label">实际还车</span>
                <span className="info-value">
                  {dayjs(order.actualReturnTime).format('YYYY-MM-DD HH:mm')}
                </span>
              </div>
            )}
            <div className="info-item">
              <span className="info-label">订单金额</span>
              <span className="info-value amount">¥{order.totalAmount?.toFixed(2)}</span>
            </div>
          </div>

          {(order.status === 0 || order.status === 1) && (
            <div className="order-actions">
              {!depositPaid && (
                <button className="btn btn-primary" onClick={handlePayDeposit}>
                  支付押金 (¥{depositAmount.toFixed(2)})
                </button>
              )}
              {depositPaid && !finalPaid && (
                <button className="btn btn-success" onClick={handlePayFinal}>
                  支付尾款
                </button>
              )}
              {depositPaid && (
                <button
                  className="btn btn-warning"
                  onClick={() => setShowReturnModal(true)}
                >
                  归还车辆
                </button>
              )}
              <button className="btn btn-danger" onClick={handleCancel}>
                取消订单
              </button>
            </div>
          )}
        </div>

        <div className="payment-info card">
          <div className="card-header">
            <h2>支付记录</h2>
          </div>
          {payments.length === 0 ? (
            <div className="empty-state">
              <p>暂无支付记录</p>
            </div>
          ) : (
            <div className="table-container">
              <table className="table">
                <thead>
                  <tr>
                    <th>类型</th>
                    <th>金额</th>
                    <th>时间</th>
                  </tr>
                </thead>
                <tbody>
                  {payments.map((payment) => (
                    <tr key={payment.id}>
                      <td>{payTypeMap[payment.payType] || payment.payType}</td>
                      <td>¥{payment.amount?.toFixed(2)}</td>
                      <td>{dayjs(payment.payTime).format('YYYY-MM-DD HH:mm')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {showReturnModal && (
        <ReturnModal
          order={order}
          stores={stores}
          onClose={() => setShowReturnModal(false)}
          onSuccess={() => {
            setShowReturnModal(false)
            loadData(order.id)
          }}
        />
      )}
    </div>
  )
}
