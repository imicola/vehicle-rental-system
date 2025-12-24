import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Home.css'

export default function Home() {
  const { user } = useAuth()

  return (
    <div className="home">
      <section className="hero">
        <div className="hero-content">
          <h1>开启您的尊贵旅程</h1>
          <p>精选豪华座驾，灵活租赁方案，让每一次出行都成为享受。</p>
          <div className="hero-actions">
            <Link to="/vehicles" className="btn btn-primary btn-lg">
              立即选车
            </Link>
            {!user && (
              <Link to="/register" className="btn btn-outline-light btn-lg">
                注册会员
              </Link>
            )}
          </div>
        </div>
      </section>

      <section className="features container">
        <div className="section-header">
          <h2>为什么选择我们</h2>
          <p>致力于为您提供最优质的租车体验</p>
        </div>
        <div className="grid grid-cols-3">
          <div className="feature-card">
            <div className="feature-icon-wrapper">
              <span className="feature-icon">🚗</span>
            </div>
            <h3>高端车队</h3>
            <p>严选 BBA 及以上豪华品牌，定期深度保养，车况如新。</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon-wrapper">
              <span className="feature-icon">💎</span>
            </div>
            <h3>透明一口价</h3>
            <p>拒绝隐形消费，所见即所得，包含基础保险与服务费。</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon-wrapper">
              <span className="feature-icon">🛡️</span>
            </div>
            <h3>无忧保障</h3>
            <p>24小时道路救援，全程管家式服务，异地还车更便捷。</p>
          </div>
        </div>
      </section>

      <section className="cta">
        <div className="cta-content">
          <h2>准备好出发了吗？</h2>
          <p>新用户注册即享首单 9 折优惠</p>
          <Link to="/vehicles" className="btn btn-primary btn-lg">
            浏览所有车型
          </Link>
        </div>
      </section>
    </div>
  )
}
