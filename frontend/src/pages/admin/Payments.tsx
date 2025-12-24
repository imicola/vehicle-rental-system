import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { paymentApi } from '../../api/payment'
import type { Payment } from '../../types'
import dayjs from 'dayjs'
import './Admin.css'

const payTypeMap: Record<string, { label: string; class: string }> = {
  Deposit: { label: '押金', class: 'badge-info' },
  Final: { label: '尾款', class: 'badge-success' },
  Penalty: { label: '罚金', class: 'badge-danger' },
}

export default function AdminPayments() {
  const [payments, setPayments] = useState<Payment[]>([])
  const [loading, setLoading] = useState(true)
  const [filterType, setFilterType] = useState<string>('')

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const res = await paymentApi.getAll()
      setPayments(res.data)
    } catch (error) {
      console.error('加载数据失败', error)
    } finally {
      setLoading(false)
    }
  }

  const filteredPayments = filterType
    ? payments.filter((p) => p.payType === filterType)
    : payments

  const totalAmount = filteredPayments.reduce((sum, p) => sum + (p.amount || 0), 0)

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
        <h1 className="page-title">支付记录</h1>
        <Link to="/admin" className="btn btn-outline btn-sm">← 返回</Link>
      </div>

      <div className="admin-toolbar">
        <div className="admin-filters">
          <select
            className="form-select"
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
            style={{ width: 'auto' }}
          >
            <option value="">全部类型</option>
            <option value="Deposit">押金</option>
            <option value="Final">尾款</option>
            <option value="Penalty">罚金</option>
          </select>
          <span className="text-muted">
            共 {filteredPayments.length} 条记录，合计 ¥{totalAmount.toFixed(2)}
          </span>
        </div>
      </div>

      <div className="card admin-table-card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>订单ID</th>
                <th>类型</th>
                <th>金额</th>
                <th>支付时间</th>
              </tr>
            </thead>
            <tbody>
              {filteredPayments.map((payment) => (
                <tr key={payment.id}>
                  <td>{payment.id}</td>
                  <td>
                    <Link to={`/orders/${payment.orderId}`}>
                      {payment.orderId}
                    </Link>
                  </td>
                  <td>
                    <span className={`badge ${payTypeMap[payment.payType]?.class}`}>
                      {payTypeMap[payment.payType]?.label || payment.payType}
                    </span>
                  </td>
                  <td>¥{payment.amount?.toFixed(2)}</td>
                  <td>{dayjs(payment.payTime).format('YYYY-MM-DD HH:mm')}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
