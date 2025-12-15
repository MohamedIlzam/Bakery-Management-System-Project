import apiClient from '@/lib/api-client';

export interface ShopDTO {
  shopId?: string;
  shopName: string;
  ownerName: string;
  contactNo: string;
  address: string;
}

export const shopService = {
  create: async (shop: ShopDTO): Promise<ShopDTO> => {
    const response = await apiClient.post<ShopDTO>('/shops', shop);
    return response.data;
  },

  update: async (id: string, shop: ShopDTO): Promise<ShopDTO> => {
    const response = await apiClient.put<ShopDTO>(`/shops/${id}`, shop);
    return response.data;
  },

  get: async (id: string): Promise<ShopDTO> => {
    const response = await apiClient.get<ShopDTO>(`/shops/${id}`);
    return response.data;
  },

  list: async (): Promise<ShopDTO[]> => {
    const response = await apiClient.get<ShopDTO[]>('/shops');
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/shops/${id}`);
  },
};

