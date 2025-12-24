import { Link } from 'react-router-dom'
import './Admin.css'

export default function AdminDashboard() {
  const menuItems = [
    { path: '/admin/vehicles', icon: '🚗', title: '车辆管理', desc: '查看和管理所有车辆' },
    { path: '/admin/orders', icon: '📋', title: '订单管理', desc: '查看所有订单记录' },
    { path: '/admin/stores', icon: '🏪', title: '门店管理', desc: '管理门店信息' },
    { path: '/admin/maintenance', icon: '🔧', title: '维护管理', desc: '车辆维护记录' },
    { path: '/admin/payments', icon: '💰', title: '支付记录', desc: '查看所有支付记录' },
  ]

  return (
    <div className="admin-dashboard">
      <div className="page-header">
        <h1 className="page-title">管理后台</h1>
      </div>

      <div className="grid grid-cols-3">
        {menuItems.map((item) => (
          <Link key={item.path} to={item.path} className="admin-card card">
            <span className="admin-card-icon">{item.icon}</span>
            <h3>{item.title}</h3>
            <p>{item.desc}</p>
          </Link>
        ))}
      </div>
    </div>
  )
}
