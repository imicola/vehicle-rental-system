import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { vehicleApi, categoryApi } from '../../api/vehicle'
import { storeApi } from '../../api/store'
import type { Vehicle, Store, Category, VehicleDTO } from '../../types'
import './Admin.css'

const statusMap: Record<number, { label: string; class: string }> = {
  0: { label: '可用', class: 'badge-success' },
  1: { label: '已租出', class: 'badge-warning' },
  2: { label: '维护中', class: 'badge-info' },
  3: { label: '已下架', class: 'badge-secondary' },
}

export default function AdminVehicles() {
  const [vehicles, setVehicles] = useState<Vehicle[]>([])
  const [stores, setStores] = useState<Store[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [showAddModal, setShowAddModal] = useState(false)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const [vehiclesRes, storesRes, categoriesRes] = await Promise.all([
        vehicleApi.getAll(),
        storeApi.getAll(),
        categoryApi.getAll(),
      ])
      setVehicles(vehiclesRes.data)
      setStores(storesRes.data)
      setCategories(categoriesRes.data)
    } catch (error) {
      console.error('加载数据失败', error)
    } finally {
      setLoading(false)
    }
  }

  const getCategoryName = (id: number) => categories.find((c) => c.id === id)?.name || '-'
  const getStoreName = (id: number) => stores.find((s) => s.id === id)?.name || '-'

  const handleStatusChange = async (vehicle: Vehicle, newStatus: number) => {
    try {
      await vehicleApi.updateStatus(vehicle.id, newStatus)
      loadData()
    } catch (error) {
      console.error('更新状态失败', error)
      alert('更新失败，请稍后重试')
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
        <h1 className="page-title">车辆管理</h1>
        <Link to="/admin" className="btn btn-outline btn-sm">← 返回</Link>
      </div>

      <div className="admin-toolbar">
        <div className="admin-filters">
          <span className="text-muted">共 {vehicles.length} 辆车</span>
        </div>
        <button className="btn btn-primary" onClick={() => setShowAddModal(true)}>
          + 添加车辆
        </button>
      </div>

      <div className="card admin-table-card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>品牌</th>
                <th>型号</th>
                <th>类型</th>
                <th>所在门店</th>
                <th>日租金</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {vehicles.map((vehicle) => (
                <tr key={vehicle.id}>
                  <td>{vehicle.id}</td>
                  <td>{vehicle.brand}</td>
                  <td>{vehicle.model}</td>
                  <td>{getCategoryName(vehicle.categoryId)}</td>
                  <td>{getStoreName(vehicle.storeId)}</td>
                  <td>¥{vehicle.dailyRate}</td>
                  <td>
                    <span className={`badge ${statusMap[vehicle.status]?.class}`}>
                      {statusMap[vehicle.status]?.label}
                    </span>
                  </td>
                  <td>
                    <div className="action-buttons">
                      {vehicle.status !== 0 && vehicle.status !== 1 && (
                        <button
                          className="btn btn-success btn-sm"
                          onClick={() => handleStatusChange(vehicle, 0)}
                        >
                          上架
                        </button>
                      )}
                      {vehicle.status === 0 && (
                        <button
                          className="btn btn-secondary btn-sm"
                          onClick={() => handleStatusChange(vehicle, 3)}
                        >
                          下架
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {showAddModal && (
        <AddVehicleModal
          stores={stores}
          categories={categories}
          onClose={() => setShowAddModal(false)}
          onSuccess={() => {
            setShowAddModal(false)
            loadData()
          }}
        />
      )}
    </div>
  )
}

// 添加车辆弹窗
function AddVehicleModal({
  stores,
  categories,
  onClose,
  onSuccess,
}: {
  stores: Store[]
  categories: Category[]
  onClose: () => void
  onSuccess: () => void
}) {
  const [form, setForm] = useState<VehicleDTO>({
    plateNumber: '',
    brand: '',
    model: '',
    categoryId: categories[0]?.id || 0,
    storeId: stores[0]?.id || 0,
    dailyRate: 0,
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      await vehicleApi.create(form)
      onSuccess()
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } }
      setError(error.response?.data?.message || '添加失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">添加车辆</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {error && <div className="auth-error">{error}</div>}
            <div className="form-group">
              <label className="form-label">车牌号</label>
              <input
                type="text"
                className="form-input"
                value={form.plateNumber}
                onChange={(e) => setForm({ ...form, plateNumber: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">品牌</label>
              <input
                type="text"
                className="form-input"
                value={form.brand}
                onChange={(e) => setForm({ ...form, brand: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">型号</label>
              <input
                type="text"
                className="form-input"
                value={form.model}
                onChange={(e) => setForm({ ...form, model: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">类型</label>
              <select
                className="form-select"
                value={form.categoryId}
                onChange={(e) => setForm({ ...form, categoryId: Number(e.target.value) })}
              >
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>{cat.name}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">所在门店</label>
              <select
                className="form-select"
                value={form.storeId}
                onChange={(e) => setForm({ ...form, storeId: Number(e.target.value) })}
              >
                {stores.map((store) => (
                  <option key={store.id} value={store.id}>{store.name}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">日租金 (元)</label>
              <input
                type="number"
                className="form-input"
                value={form.dailyRate}
                onChange={(e) => setForm({ ...form, dailyRate: Number(e.target.value) })}
                min="0"
                step="0.01"
                required
              />
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-outline" onClick={onClose}>取消</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '添加中...' : '添加'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
