import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { vehicleApi, categoryApi } from '../api/vehicle'
import { storeApi } from '../api/store'
import type { Vehicle, Store, Category } from '../types'
import RentalModal from '../components/RentalModal'
import './VehicleList.css'

const statusMap: Record<number, { label: string; class: string }> = {
  0: { label: '可租', class: 'badge-success' },
  1: { label: '已租出', class: 'badge-warning' },
  2: { label: '维护中', class: 'badge-info' },
  3: { label: '已下架', class: 'badge-secondary' },
}

export default function VehicleList() {
  const [vehicles, setVehicles] = useState<Vehicle[]>([])
  const [stores, setStores] = useState<Store[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [selectedStore, setSelectedStore] = useState<string>('')
  const [startTime, setStartTime] = useState('')
  const [endTime, setEndTime] = useState('')
  const [selectedVehicle, setSelectedVehicle] = useState<Vehicle | null>(null)
  const [showModal, setShowModal] = useState(false)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const [vehiclesRes, storesRes, categoriesRes] = await Promise.all([
        vehicleApi.search({}),
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

  const handleSearch = async () => {
    setLoading(true)
    try {
      const params: { storeId?: number; start?: string; end?: string } = {}
      if (selectedStore) params.storeId = Number(selectedStore)
      if (startTime) params.start = startTime
      if (endTime) params.end = endTime
      const res = await vehicleApi.search(params)
      setVehicles(res.data)
    } catch (error) {
      console.error('搜索失败', error)
    } finally {
      setLoading(false)
    }
  }

  const handleRent = (vehicle: Vehicle) => {
    setSelectedVehicle(vehicle)
    setShowModal(true)
  }

  const getCategoryName = (categoryId: number) => {
    return categories.find((c) => c.id === categoryId)?.name || '-'
  }

  const getStoreName = (storeId: number) => {
    return stores.find((s) => s.id === storeId)?.name || '-'
  }

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
      </div>
    )
  }

  return (
    <div className="vehicle-list-page">
      <div className="page-header">
        <h1 className="page-title">车辆列表</h1>
      </div>

      <div className="search-bar card">
        <div className="search-filters">
          <div className="filter-group">
            <label className="form-label">取车门店</label>
            <select
              className="form-select"
              value={selectedStore}
              onChange={(e) => setSelectedStore(e.target.value)}
            >
              <option value="">全部门店</option>
              {stores.map((store) => (
                <option key={store.id} value={store.id}>
                  {store.name}
                </option>
              ))}
            </select>
          </div>
          <div className="filter-group">
            <label className="form-label">开始时间</label>
            <input
              type="datetime-local"
              className="form-input"
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
            />
          </div>
          <div className="filter-group">
            <label className="form-label">结束时间</label>
            <input
              type="datetime-local"
              className="form-input"
              value={endTime}
              onChange={(e) => setEndTime(e.target.value)}
            />
          </div>
          <div className="filter-group filter-actions">
            <button className="btn btn-primary" onClick={handleSearch}>
              搜索
            </button>
          </div>
        </div>
      </div>

      {vehicles.length === 0 ? (
        <div className="empty-state card">
          <div className="empty-state-icon">🚗</div>
          <p>暂无可用车辆</p>
        </div>
      ) : (
        <div className="grid grid-cols-3">
          {vehicles.map((vehicle) => (
            <div key={vehicle.id} className="vehicle-card card">
              <div className="vehicle-image">
                <span className="vehicle-emoji">🚗</span>
              </div>
              <div className="vehicle-info">
                <div className="vehicle-header">
                  <h3>{vehicle.brand} {vehicle.model}</h3>
                  <span className={`badge ${statusMap[vehicle.status]?.class}`}>
                    {statusMap[vehicle.status]?.label}
                  </span>
                </div>
                <div className="vehicle-meta">
                  <span>📂 {getCategoryName(vehicle.categoryId)}</span>
                  <span>📍 {getStoreName(vehicle.storeId)}</span>
                </div>
                <div className="vehicle-price">
                  <span className="price">¥{vehicle.dailyRate}</span>
                  <span className="unit">/天</span>
                </div>
                <div className="vehicle-actions">
                  <Link to={`/vehicles/${vehicle.id}`} className="btn btn-outline btn-sm">
                    详情
                  </Link>
                  {vehicle.status === 0 && (
                    <button
                      className="btn btn-primary btn-sm"
                      onClick={() => handleRent(vehicle)}
                    >
                      立即租车
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {showModal && selectedVehicle && (
        <RentalModal
          vehicle={selectedVehicle}
          stores={stores}
          defaultStartTime={startTime}
          defaultEndTime={endTime}
          onClose={() => setShowModal(false)}
          onSuccess={() => {
            setShowModal(false)
            handleSearch()
          }}
        />
      )}
    </div>
  )
}
