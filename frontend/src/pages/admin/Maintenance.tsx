import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { maintenanceApi } from '../../api/maintenance'
import { vehicleApi } from '../../api/vehicle'
import type { Maintenance, Vehicle } from '../../types'
import dayjs from 'dayjs'
import './Admin.css'

export default function AdminMaintenance() {
  const [records, setRecords] = useState<Maintenance[]>([])
  const [vehicles, setVehicles] = useState<Vehicle[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const [recordsRes, vehiclesRes] = await Promise.all([
        maintenanceApi.getAll(),
        vehicleApi.getAll(),
      ])
      setRecords(recordsRes.data)
      setVehicles(vehiclesRes.data)
    } catch (error) {
      console.error('加载数据失败', error)
    } finally {
      setLoading(false)
    }
  }

  const getVehicleName = (id: number) => {
    const v = vehicles.find((v) => v.id === id)
    return v ? `${v.brand} ${v.model}` : '-'
  }

  const handleComplete = async (id: number) => {
    if (!confirm('确认完成此维护？')) return
    try {
      await maintenanceApi.complete(id)
      loadData()
    } catch (error) {
      console.error('操作失败', error)
      alert('操作失败，请稍后重试')
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
    <div className="admin-page">
      <div className="page-header">
        <h1 className="page-title">维护管理</h1>
        <Link to="/admin" className="btn btn-outline btn-sm">← 返回</Link>
      </div>

      <div className="admin-toolbar">
        <div className="admin-filters">
          <span className="text-muted">共 {records.length} 条记录</span>
        </div>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          + 新建维护
        </button>
      </div>

      <div className="card admin-table-card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>车辆</th>
                <th>类型</th>
                <th>描述</th>
                <th>费用</th>
                <th>开始日期</th>
                <th>结束日期</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {records.map((record) => (
                <tr key={record.id}>
                  <td>{record.id}</td>
                  <td>{getVehicleName(record.vehicleId)}</td>
                  <td>{record.type}</td>
                  <td>{record.description || '-'}</td>
                  <td>¥{record.cost?.toFixed(2)}</td>
                  <td>{dayjs(record.startDate).format('YYYY-MM-DD')}</td>
                  <td>
                    {record.endDate ? (
                      dayjs(record.endDate).format('YYYY-MM-DD')
                    ) : (
                      <span className="badge badge-warning">进行中</span>
                    )}
                  </td>
                  <td>
                    {!record.endDate && (
                      <button
                        className="btn btn-success btn-sm"
                        onClick={() => handleComplete(record.id)}
                      >
                        完成
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <MaintenanceModal
          vehicles={vehicles}
          onClose={() => setShowModal(false)}
          onSuccess={() => {
            setShowModal(false)
            loadData()
          }}
        />
      )}
    </div>
  )
}

function MaintenanceModal({
  vehicles,
  onClose,
  onSuccess,
}: {
  vehicles: Vehicle[]
  onClose: () => void
  onSuccess: () => void
}) {
  const [vehicleId, setVehicleId] = useState(vehicles[0]?.id || 0)
  const [type, setType] = useState('')
  const [description, setDescription] = useState('')
  const [cost, setCost] = useState(0)
  const [startDate, setStartDate] = useState(dayjs().format('YYYY-MM-DD'))
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  // 只显示可用的车辆
  const availableVehicles = vehicles.filter((v) => v.status === 0)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      await maintenanceApi.create({
        vehicleId,
        type,
        startDate,
        cost,
        description: description || undefined,
      })
      onSuccess()
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } }
      setError(error.response?.data?.message || '操作失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">新建维护记录</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {error && <div className="auth-error">{error}</div>}
            <div className="form-group">
              <label className="form-label">车辆</label>
              <select
                className="form-select"
                value={vehicleId}
                onChange={(e) => setVehicleId(Number(e.target.value))}
                required
              >
                {availableVehicles.map((v) => (
                  <option key={v.id} value={v.id}>
                    {v.brand} {v.model}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">维护类型</label>
              <input
                type="text"
                className="form-input"
                value={type}
                onChange={(e) => setType(e.target.value)}
                placeholder="如：保养、维修、年检"
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">描述</label>
              <input
                type="text"
                className="form-input"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="可选"
              />
            </div>
            <div className="form-group">
              <label className="form-label">费用 (元)</label>
              <input
                type="number"
                className="form-input"
                value={cost}
                onChange={(e) => setCost(Number(e.target.value))}
                min="0"
                step="0.01"
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">开始日期</label>
              <input
                type="date"
                className="form-input"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                required
              />
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-outline" onClick={onClose}>取消</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '保存中...' : '保存'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
