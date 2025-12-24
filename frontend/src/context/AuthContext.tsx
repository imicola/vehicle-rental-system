import { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import type { User, LoginRequest, RegisterDTO } from '../types'
import { authApi } from '../api/auth'

interface AuthContextType {
  user: User | null
  token: string | null
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterDTO) => Promise<void>
  logout: () => void
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    if (savedToken && savedUser) {
      setToken(savedToken)
      setUser(JSON.parse(savedUser))
    }
    setIsLoading(false)
  }, [])

  const login = async (data: LoginRequest) => {
    const response = await authApi.login(data)
    const { token: newToken, userId, username, role, phone } = response.data
    const newUser: User = { id: userId, username, role, phone }
    
    setToken(newToken)
    setUser(newUser)
    localStorage.setItem('token', newToken)
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  const register = async (data: RegisterDTO) => {
    await authApi.register(data)
  }

  const logout = () => {
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
