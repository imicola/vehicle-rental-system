import { Outlet, Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Layout.css'

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="layout">
      <header className="header">
        <div className="container">
          <nav className="nav">
            <Link to="/" className="logo">
              🚗 汽车租赁
            </Link>
            <div className="nav-links">
              <Link to="/vehicles">车辆列表</Link>
              {user && <Link to="/orders">我的订单</Link>}
              {user?.role === 'ADMIN' && <Link to="/admin">管理后台</Link>}
            </div>
            <div className="nav-actions">
              {user ? (
                <>
                  <span className="user-info">
                    👤 {user.username}
                    {user.role === 'ADMIN' && <span className="badge badge-info">管理员</span>}
                  </span>
                  <button onClick={handleLogout} className="btn btn-outline btn-sm">
                    退出
                  </button>
                </>
              ) : (
                <>
                  <Link to="/login" className="btn btn-outline btn-sm">登录</Link>
                  <Link to="/register" className="btn btn-primary btn-sm">注册</Link>
                </>
              )}
            </div>
          </nav>
        </div>
      </header>
      <main className="main">
        <div className="container">
          <Outlet />
        </div>
      </main>
      <footer className="footer">
        <div className="container">
          <p>© 2025 汽车租赁管理系统</p>
        </div>
      </footer>
    </div>
  )
}
