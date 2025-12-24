import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { storeApi } from '../../api/store'
import type { Store, StoreDTO } from '../../types'
import './Admin.css'

export default function AdminStores() {
  const [stores, setStores] = useState<Store[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editStore, setEditStore] = useState<Store | null>(null)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const res = await storeApi.getAll()
      setStores(res.data)
    } catch (error) {
      console.error('加载数据失败', error)
    } finally {
      setLoading(false)
    }
  }

  const handleAdd = () => {
    setEditStore(null)
    setShowModal(true)
  }

  const handleEdit = (store: Store) => {
    setEditStore(store)
    setShowModal(true)
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
        <h1 className="page-title">门店管理</h1>
        <Link to="/admin" className="btn btn-outline btn-sm">← 返回</Link>
      </div>

      <div className="admin-toolbar">
        <div className="admin-filters">
          <span className="text-muted">共 {stores.length} 家门店</span>
        </div>
        <button className="btn btn-primary" onClick={handleAdd}>
          + 添加门店
        </button>
      </div>

      <div className="card admin-table-card">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>名称</th>
                <th>地址</th>
                <th>电话</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {stores.map((store) => (
                <tr key={store.id}>
                  <td>{store.id}</td>
                  <td>{store.name}</td>
                  <td>{store.address || '-'}</td>
                  <td>{store.phone || '-'}</td>
                  <td>
                    <button
                      className="btn btn-outline btn-sm"
                      onClick={() => handleEdit(store)}
                    >
                      编辑
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <StoreModal
          store={editStore}
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

function StoreModal({
  store,
  onClose,
  onSuccess,
}: {
  store: Store | null
  onClose: () => void
  onSuccess: () => void
}) {
  const [form, setForm] = useState<StoreDTO>({
    name: store?.name || '',
    address: store?.address || '',
    phone: store?.phone || '',
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      if (store) {
        await storeApi.update(store.id, form)
      } else {
        await storeApi.create(form)
      }
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
          <h2 className="modal-title">{store ? '编辑门店' : '添加门店'}</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {error && <div className="auth-error">{error}</div>}
            <div className="form-group">
              <label className="form-label">名称</label>
              <input
                type="text"
                className="form-input"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">地址</label>
              <input
                type="text"
                className="form-input"
                value={form.address}
                onChange={(e) => setForm({ ...form, address: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">电话</label>
              <input
                type="tel"
                className="form-input"
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
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
