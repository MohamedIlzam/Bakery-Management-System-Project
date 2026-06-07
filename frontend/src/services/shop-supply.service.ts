import apiClient from '@/lib/api-client';

export interface ShopSupplyItemDTO {
  productId: string;
  productName: string;
  quantity: number;
  returnQuantity?: number;
  price: number;
  shopId?: string;
}

export interface ShopSupplyRequestDTO {
  shopId: string;
  salesmanId: string;
  driverId?: string;  // Backend expects userId, not driverId
  vehicleId: string;
  items: ShopSupplyItemDTO[];
  supplyDate?: string;
}

export interface ShopSupplyResponseDTO {
  salesmanId: any;
  supplyId: string;
  supplyDate: string;
  driverId: string;        // Changed back to driverId
  driverName: string;
  vehicleId: string;
  vehicleNo: string;
  shopId?: string;
  shopName: string;
  totalAmount: number;
  items: ShopSupplyItemDTO[];
}

export const shopSupplyService = {
  create: async (supply: ShopSupplyRequestDTO): Promise<ShopSupplyResponseDTO> => {
    const response = await apiClient.post<ShopSupplyResponseDTO>('/shop-supplies', supply);
    return response.data;
  },

  get: async (id: string): Promise<ShopSupplyResponseDTO> => {
    const response = await apiClient.get<ShopSupplyResponseDTO>(`/shop-supplies/${id}`);
    return response.data;
  },

  list: async (): Promise<ShopSupplyResponseDTO[]> => {
    const response = await apiClient.get<ShopSupplyResponseDTO[]>('/shop-supplies');
    return response.data;
  },

  update: async (id: string, supply: ShopSupplyRequestDTO): Promise<ShopSupplyResponseDTO> => {
    const response = await apiClient.put<ShopSupplyResponseDTO>(`/shop-supplies/${id}`, supply);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/shop-supplies/${id}`);
  },
};