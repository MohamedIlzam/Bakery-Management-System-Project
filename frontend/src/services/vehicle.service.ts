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
};
