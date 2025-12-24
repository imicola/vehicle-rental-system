import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import Login from './pages/Login'
import Register from './pages/Register'
import Home from './pages/Home'
import VehicleList from './pages/VehicleList'
import VehicleDetail from './pages/VehicleDetail'
import MyOrders from './pages/MyOrders'
import OrderDetail from './pages/OrderDetail'
import AdminDashboard from './pages/admin/Dashboard'
import AdminVehicles from './pages/admin/Vehicles'
import AdminOrders from './pages/admin/Orders'
import AdminStores from './pages/admin/Stores'
import AdminMaintenance from './pages/admin/Maintenance'
import AdminPayments from './pages/admin/Payments'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { user } = useAuth()
  return user ? <>{children}</> : <Navigate to="/login" />
}

function AdminRoute({ children }: { children: React.ReactNode }) {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" />
  if (user.role !== 'ADMIN') return <Navigate to="/" />
  return <>{children}</>
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/" element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="vehicles" element={<VehicleList />} />
        <Route path="vehicles/:id" element={<VehicleDetail />} />
        <Route path="orders" element={<PrivateRoute><MyOrders /></PrivateRoute>} />
        <Route path="orders/:id" element={<PrivateRoute><OrderDetail /></PrivateRoute>} />
        <Route path="admin" element={<AdminRoute><AdminDashboard /></AdminRoute>} />
        <Route path="admin/vehicles" element={<AdminRoute><AdminVehicles /></AdminRoute>} />
        <Route path="admin/orders" element={<AdminRoute><AdminOrders /></AdminRoute>} />
        <Route path="admin/stores" element={<AdminRoute><AdminStores /></AdminRoute>} />
        <Route path="admin/maintenance" element={<AdminRoute><AdminMaintenance /></AdminRoute>} />
        <Route path="admin/payments" element={<AdminRoute><AdminPayments /></AdminRoute>} />
      </Route>
    </Routes>
  )
}
