import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { orderApi } from '../api/order'
import type { Vehicle, Store } from '../types'
import dayjs from 'dayjs'

interface RentalModalProps {
  vehicle: Vehicle
  stores: Store[]
  defaultStartTime: string
  defaultEndTime: string
  onClose: () => void
  onSuccess: () => void
}

export default function RentalModal({
  vehicle,
  stores,
  defaultStartTime,
  defaultEndTime,
  onClose,
  onSuccess,
}: RentalModalProps) {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [pickupStoreId, setPickupStoreId] = useState(String(vehicle.storeId))
  const [returnStoreId, setReturnStoreId] = useState(String(vehicle.storeId))
  const [startTime, setStartTime] = useState(
    defaultStartTime || dayjs().add(1, 'hour').format('YYYY-MM-DDTHH:00')
  )
  const [endTime, setEndTime] = useState(
    defaultEndTime || dayjs().add(1, 'day').add(1, 'hour').format('YYYY-MM-DDTHH:00')
  )
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  // 计算天数和费用
  const calculateDays = () => {
    if (!startTime || !endTime) return 0
    const start = dayjs(startTime)
    const end = dayjs(endTime)
    const days = Math.ceil(end.diff(start, 'hour') / 24)
    return Math.max(days, 1)
  }

  const days = calculateDays()
  const totalAmount = days * vehicle.dailyRate
  const depositAmount = vehicle.dailyRate * 3

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    if (!user) {
      navigate('/login')
      return
    }

    if (dayjs(endTime).isBefore(dayjs(startTime))) {
      setError('结束时间必须晚于开始时间')
      return
    }

    setLoading(true)
    try {
      await orderApi.create({
        userId: user.id,
        vehicleId: vehicle.id,
        pickupStoreId: Number(pickupStoreId),
        returnStoreId: Number(returnStoreId),
        startTime: dayjs(startTime).format('YYYY-MM-DD HH:mm:ss'),
        endTime: dayjs(endTime).format('YYYY-MM-DD HH:mm:ss'),
      })
      onSuccess()
      navigate('/orders')
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } }
      setError(error.response?.data?.message || '下单失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">租赁车辆</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {error && <div className="auth-error">{error}</div>}

            <div className="rental-vehicle-info">
              <h3>🚗 {vehicle.brand} {vehicle.model}</h3>
              <p className="text-muted">日租金：¥{vehicle.dailyRate}/天</p>
            </div>

            <div className="form-group">
              <label className="form-label">取车门店</label>
              <select
                className="form-select"
                value={pickupStoreId}
                onChange={(e) => setPickupStoreId(e.target.value)}
                required
              >
                {stores.map((store) => (
                  <option key={store.id} value={store.id}>
                    {store.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">还车门店</label>
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

            <div className="form-group">
              <label className="form-label">取车时间</label>
              <input
                type="datetime-local"
                className="form-input"
                value={startTime}
                onChange={(e) => setStartTime(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">还车时间</label>
              <input
                type="datetime-local"
                className="form-input"
                value={endTime}
                onChange={(e) => setEndTime(e.target.value)}
                required
              />
            </div>

            <div className="rental-summary">
              <div className="summary-row">
                <span>租赁天数</span>
                <span>{days} 天</span>
              </div>
              <div className="summary-row">
                <span>押金 (日租金 × 3)</span>
                <span>¥{depositAmount.toFixed(2)}</span>
              </div>
              <div className="summary-row total">
                <span>预计租金</span>
                <span>¥{totalAmount.toFixed(2)}</span>
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-outline" onClick={onClose}>
              取消
            </button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '提交中...' : '确认下单'}
            </button>
          </div>
        </form>
      </div>

      <style>{`
        .rental-vehicle-info {
          background: var(--bg);
          padding: 1rem;
          border-radius: var(--radius);
          margin-bottom: 1rem;
        }
        .rental-vehicle-info h3 {
          margin-bottom: 0.25rem;
        }
        .rental-summary {
          background: var(--bg);
          padding: 1rem;
          border-radius: var(--radius);
          margin-top: 1rem;
        }
        .summary-row {
          display: flex;
          justify-content: space-between;
          padding: 0.5rem 0;
          border-bottom: 1px solid var(--border);
        }
        .summary-row:last-child {
          border-bottom: none;
        }
        .summary-row.total {
          font-weight: 600;
          font-size: 1.125rem;
          color: var(--primary);
        }
      `}</style>
    </div>
  )
}
