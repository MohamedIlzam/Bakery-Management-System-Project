import apiClient from '@/lib/api-client';

export interface SaleItemDTO {
  productName: string;
  quantity: number;
  price: number;
}

export interface SaleRequestDTO {
  shopId?: string;        // NEW: Preferred way - use shop ID directly
  shopName?: string;      // Legacy: for backwards compatibility
  ownerName?: string;     // Legacy: for backwards compatibility
  contactNo?: string;     // Legacy: for backwards compatibility
  
  driverId?: string;      // NEW: Preferred way - use driver ID directly
  driverName?: string;    // Legacy: for backwards compatibility
  
  vehicleNo?: string;     // Optional: for backwards compatibility
  items: SaleItemDTO[];
}

export interface SaleResponseDTO {
  saleId: string;
  date: string;
  shopId?: string;
  shopName: string;
  vehicleNo: string;
  driverId?: string;
  driverName: string;
  status: 'OUT' | 'RETURN';
  totalAmount: number;
  items: SaleItemDTO[];
}

export interface SaleUpdateDTO {
  status?: 'OUT' | 'RETURN';
  items?: SaleItemDTO[];
}

export const saleService = {
  create: async (sale: SaleRequestDTO): Promise<SaleResponseDTO> => {
    const response = await apiClient.post<SaleResponseDTO>('/sales', sale);
    return response.data;
  },

  get: async (id: string): Promise<SaleResponseDTO> => {
    const response = await apiClient.get<SaleResponseDTO>(`/sales/${id}`);
    return response.data;
  },

  list: async (): Promise<SaleResponseDTO[]> => {
    const response = await apiClient.get<SaleResponseDTO[]>('/sales');
    return response.data;
  },

  getByDate: async (date: string): Promise<SaleResponseDTO[]> => {
    const response = await apiClient.get<SaleResponseDTO[]>('/sales/by-date', {
      params: { date },
    });
    return response.data;
  },

  getByDateRange: async (startDate: string, endDate: string): Promise<SaleResponseDTO[]> => {
    const response = await apiClient.get<SaleResponseDTO[]>('/sales/by-date-range', {
      params: { startDate, endDate },
    });
    return response.data;
  },

  update: async (id: string, sale: SaleUpdateDTO): Promise<SaleResponseDTO> => {
    const response = await apiClient.put<SaleResponseDTO>(`/sales/${id}`, sale);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/sales/${id}`);
  },

  deleteByDate: async (date: string): Promise<void> => {
    await apiClient.delete('/sales/by-date', {
      params: { date },
    });
  },
};
