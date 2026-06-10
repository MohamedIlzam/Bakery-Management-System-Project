import apiClient from '@/lib/api-client';

export interface VehicleDTO {
  vehicleId?: string;    // Matches 'vehicle_id' from SQL
  vehicleNo: string;     // Matches 'vehicle_no' from SQL
  type: string;          // Matches 'type' from SQL (e.g., 'Mini Truck', 'Van')
  driverName?: string;   // Matches 'driver_name' from SQL
  vehicleType: string;   // Matches 'vehicle_type' from SQL (e.g., 'food_truck', 'shop delivery')
}

export const vehicleService = {
  list: async (): Promise<VehicleDTO[]> => {
    const response = await apiClient.get<VehicleDTO[]>('/vehicles');
    return response.data;
  },

  get: async (id: string): Promise<VehicleDTO> => {
    const response = await apiClient.get<VehicleDTO>(`/vehicles/${id}`);
    return response.data;
  },

  create: async (payload: Omit<VehicleDTO, 'vehicleId'>): Promise<VehicleDTO> => {
    const response = await apiClient.post<VehicleDTO>('/vehicles', payload);
    return response.data;
  },

  update: async (id: string, payload: Omit<VehicleDTO, 'vehicleId'>): Promise<VehicleDTO> => {
    const response = await apiClient.put<VehicleDTO>(`/vehicles/${id}`, payload);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/vehicles/${id}`);
  },
};
