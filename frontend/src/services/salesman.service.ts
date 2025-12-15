import apiClient from '@/lib/api-client';

export interface SalesmanDTO {
  userId?: string;
  username: string;
  role: string; // e.g., ROLE_SALESMAN, ROLE_DRIVER
  password?: string;
}

export const salesmanService = {
  create: async (salesman: { username: string; role: string; password?: string }): Promise<SalesmanDTO> => {
    const response = await apiClient.post<SalesmanDTO>('/salesman/create', salesman);
    return response.data;
  },

  update: async (id: string, salesman: { username: string; role?: string }): Promise<SalesmanDTO> => {
    const response = await apiClient.put<SalesmanDTO>(`/salesman/${id}`, salesman);
    return response.data;
  },

  get: async (id: string): Promise<SalesmanDTO> => {
    const response = await apiClient.get<SalesmanDTO>(`/salesman/${id}`);
    return response.data;
  },

  list: async (): Promise<SalesmanDTO[]> => {
    const response = await apiClient.get<SalesmanDTO[]>('/salesman/all');
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/salesman/${id}`);
  },
};
