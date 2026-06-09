import apiClient from '@/lib/api-client';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  success: boolean;
  role: string;
  message: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  role?: string;
}

export interface RegisterResponse {
  message: string;
}

export const authService = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await apiClient.post<LoginResponse>('/auth/login', credentials);
    return response.data;
  },

  register: async (payload: RegisterRequest): Promise<RegisterResponse> => {
    const response = await apiClient.post<RegisterResponse>('/auth/register', payload);
    return response.data;
  },

  logout: async (): Promise<void> => {
    // Since the backend uses session-based auth, we might need to add a logout endpoint
    // For now, we'll just clear the client-side state
    localStorage.removeItem('userRole');
    localStorage.removeItem('username');
  },
};

