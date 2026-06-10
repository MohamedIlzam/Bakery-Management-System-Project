import apiClient from "@/lib/api-client";

export interface DriverDTO {
  driverId: string;
  name: string;
  contact?: string;
}

export const driverService = {
  list: async (): Promise<DriverDTO[]> => {
    const response = await apiClient.get<DriverDTO[]>("/drivers");
    return response.data;
  },
  
  get: async (id: string): Promise<DriverDTO> => {
    const response = await apiClient.get<DriverDTO>(`/drivers/${id}`);
    return response.data;
  },

  create: async (payload: Omit<DriverDTO, 'driverId'>): Promise<DriverDTO> => {
    const response = await apiClient.post<DriverDTO>("/drivers", payload);
    return response.data;
  },

  update: async (id: string, payload: Omit<DriverDTO, 'driverId'>): Promise<DriverDTO> => {
    const response = await apiClient.put<DriverDTO>(`/drivers/${id}`, payload);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/drivers/${id}`);
  },
};
