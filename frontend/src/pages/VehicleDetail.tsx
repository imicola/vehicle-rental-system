import { useState, useEffect } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { vehicleApi, categoryApi } from '../api/vehicle'
import { storeApi } from '../api/store'
import type { Vehicle, Store, Category } from '../types'
import RentalModal from '../components/RentalModal'
import './VehicleDetail.css'

const statusMap: Record<number, { label: string; class: string }> = {
  0: { label: '可租', class: 'badge-success' },
  1: { label: '已租出', class: 'badge-warning' },
  2: { label: '维护中', class: 'badge-info' },
  3: { label: '已下架', class: 'badge-secondary' },
}

export default function VehicleDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [vehicle, setVehicle] = useState<Vehicle | null>(null)
  const [store, setStore] = useState<Store | null>(null)
  const [category, setCategory] = useState<Category | null>(null)
  const [stores, setStores] = useState<Store[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)

  useEffect(() => {
    if (id) loadData(Number(id))
  }, [id])

  const loadData = async (vehicleId: number) => {
    try {
      const [vehicleRes, storesRes, categoriesRes] = await Promise.all([
        vehicleApi.getById(vehicleId),
        storeApi.getAll(),
        categoryApi.getAll(),
      ])
      const v = vehicleRes.data
      setVehicle(v)
      setStores(storesRes.data)
      setStore(storesRes.data.find((s) => s.id === v.storeId) || null)
      setCategory(categoriesRes.data.find((c) => c.id === v.categoryId) || null)
    } catch (error) {
      console.error('加载车辆详情失败', error)
      navigate('/vehicles')
    } finally {
      setLoading(false)
    }
  }

  if (loading || !vehicle) {
    return (
      <div className="loading">
        <div className="spinner"></div>
      </div>
    )
  }

  return (
    <div className="vehicle-detail-page">
      <div className="page-header">
        <Link to="/vehicles" className="back-link">← 返回列表</Link>
      </div>

      <div className="detail-layout">
        <div className="detail-image card">
          <div className="image-placeholder">
            <span>🚗</span>
          </div>
        </div>

        <div className="detail-info card">
          <div className="detail-header">
            <h1>{vehicle.brand} {vehicle.model}</h1>
            <span className={`badge ${statusMap[vehicle.status]?.class}`}>
              {statusMap[vehicle.status]?.label}
            </span>
          </div>

          <div className="detail-price">
            <span className="price">¥{vehicle.dailyRate}</span>
            <span className="unit">/天</span>
          </div>

          <div className="detail-meta">
            <div className="meta-item">
              <span className="meta-label">车辆类型</span>
              <span className="meta-value">{category?.name || '-'}</span>
            </div>
            <div className="meta-item">
              <span className="meta-label">所在门店</span>
              <span className="meta-value">{store?.name || '-'}</span>
            </div>
            <div className="meta-item">
              <span className="meta-label">门店地址</span>
              <span className="meta-value">{store?.address || '-'}</span>
            </div>
            <div className="meta-item">
              <span className="meta-label">门店电话</span>
              <span className="meta-value">{store?.phone || '-'}</span>
            </div>
          </div>

          {vehicle.status === 0 && (
            <button
              className="btn btn-primary btn-lg"
              style={{ width: '100%' }}
              onClick={() => setShowModal(true)}
            >
              立即租车
            </button>
          )}
        </div>
      </div>

      {showModal && (
        <RentalModal
          vehicle={vehicle}
          stores={stores}
          defaultStartTime=""
          defaultEndTime=""
          onClose={() => setShowModal(false)}
          onSuccess={() => {
            setShowModal(false)
            loadData(vehicle.id)
          }}
        />
      )}
    </div>
  )
}
