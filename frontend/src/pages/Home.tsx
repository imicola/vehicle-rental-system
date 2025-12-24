import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Home.css'

export default function Home() {
  const { user } = useAuth()

  return (
    <div className="home">
      <section className="hero">
        <h1>便捷的汽车租赁服务</h1>
        <p>提供多种车型选择，灵活的租赁方案，让您的出行更加便利</p>
        <div className="hero-actions">
          <Link to="/vehicles" className="btn btn-primary btn-lg">
            浏览车辆
          </Link>
          {!user && (
            <Link to="/register" className="btn btn-outline btn-lg">
              免费注册
            </Link>
          )}
        </div>
      </section>

      <section className="features">
        <div className="grid grid-cols-3">
          <div className="feature-card">
            <div className="feature-icon">🚗</div>
            <h3>丰富车型</h3>
            <p>从经济型到豪华型，满足您的不同需求</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">💰</div>
            <h3>透明价格</h3>
            <p>无隐藏费用，价格清晰明了</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🏪</div>
            <h3>多门店服务</h3>
            <p>支持异地取还车，出行更自由</p>
          </div>
        </div>
      </section>

      <section className="cta">
        <h2>准备好开始了吗？</h2>
        <p>立即搜索可用车辆，开启您的租车之旅</p>
        <Link to="/vehicles" className="btn btn-primary btn-lg">
          立即租车
        </Link>
      </section>
    </div>
  )
}
