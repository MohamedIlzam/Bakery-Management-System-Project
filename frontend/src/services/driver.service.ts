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
};
