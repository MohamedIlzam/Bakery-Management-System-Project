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
  recoveryEmail: string;
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

  forgotPassword: async (usernameOrEmail: string): Promise<RegisterResponse> => {
    const response = await apiClient.post<RegisterResponse>('/auth/forgot-password', { usernameOrEmail });
    return response.data;
  },

  verifyCode: async (usernameOrEmail: string, code: string): Promise<RegisterResponse> => {
    const response = await apiClient.post<RegisterResponse>('/auth/verify-code', { usernameOrEmail, code });
    return response.data;
  },

  resetPassword: async (usernameOrEmail: string, code: string, newPassword: string): Promise<RegisterResponse> => {
    const response = await apiClient.post<RegisterResponse>('/auth/reset-password', { usernameOrEmail, code, newPassword });
    return response.data;
  },

  getProfile: async (): Promise<any> => {
    const response = await apiClient.get('/salesman/profile');
    return response.data;
  },

  updateProfile: async (payload: { username?: string; password?: string }): Promise<any> => {
    const response = await apiClient.put('/salesman/profile', payload);
    return response.data;
  },

  sendEmailUpdateCode: async (email: string): Promise<any> => {
    const response = await apiClient.post('/salesman/profile/email/send-code', { email });
    return response.data;
  },

  verifyEmailUpdateCode: async (email: string, code: string): Promise<any> => {
    const response = await apiClient.post('/salesman/profile/email/verify-code', { email, code });
    return response.data;
  },

  getPendingUsers: async (): Promise<any[]> => {
    const response = await apiClient.get('/salesman/pending');
    return response.data;
  },

  approveUser: async (userId: string): Promise<any> => {
    const response = await apiClient.post(`/salesman/pending/${userId}/approve`);
    return response.data;
  },

  rejectUser: async (userId: string): Promise<any> => {
    const response = await apiClient.post(`/salesman/pending/${userId}/reject`);
    return response.data;
  },
};

