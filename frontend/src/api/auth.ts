import api from './client'
import type { LoginRequest, LoginResponse, RegisterDTO, User } from '../types'

export const authApi = {
  login: (data: LoginRequest) => 
    api.post<LoginResponse>('/auth/login', data),
  
  register: (data: RegisterDTO) => 
    api.post<User>('/auth/register', data),
}
