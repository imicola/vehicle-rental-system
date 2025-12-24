import { useState } from 'react'
import { orderApi } from '../api/order'
import type { Order, Store } from '../types'

interface ReturnModalProps {
  order: Order
  stores: Store[]
  onClose: () => void
  onSuccess: () => void
}

export default function ReturnModal({ order, stores, onClose, onSuccess }: ReturnModalProps) {
  const [returnStoreId, setReturnStoreId] = useState(String(order.returnStoreId))
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      await orderApi.returnVehicle(order.id, Number(returnStoreId))
      onSuccess()
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } }
      setError(error.response?.data?.message || '归还失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">归还车辆</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {error && <div className="auth-error">{error}</div>}

            <div className="return-info" style={{ 
              background: 'var(--bg)', 
              padding: '1rem', 
              borderRadius: 'var(--radius)',
              marginBottom: '1rem'
            }}>
              <p><strong>订单号：</strong>{order.orderNo}</p>
              <p><strong>车辆：</strong>{order.vehicle?.brand} {order.vehicle?.model}</p>
            </div>

            <div className="form-group">
              <label className="form-label">归还门店</label>
              <select
                className="form-select"
                value={returnStoreId}
                onChange={(e) => setReturnStoreId(e.target.value)}
                required
              >
                {stores.map((store) => (
                  <option key={store.id} value={store.id}>
                    {store.name}
                  </option>
                ))}
              </select>
            </div>

            <p className="text-muted text-sm" style={{ marginTop: '1rem' }}>
              ⚠️ 如果超时归还，将按日租金的1.5倍收取超时费用
            </p>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-outline" onClick={onClose}>
              取消
            </button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '处理中...' : '确认归还'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
