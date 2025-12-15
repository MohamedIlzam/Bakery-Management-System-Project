import apiClient from '@/lib/api-client';

export interface FairDeliveryItemDTO {
  itemId?: string;
  deliveryId?: string;
  productId: string;
  qtySent: number;
  qtyRemaining?: number;
  unitPrice: number;
}

export interface FairDeliveryDTO {
  deliveryId?: string;
  fairName: string;
  deliveryDate?: string; // Will be converted to LocalDate by backend
  extraPayments: number;
  tax: number;
  dieselAmount: number;
  profit?: number;
  status: string;
  vehicleId?: string; // Optional - can be null
  driverId: string;
  items: FairDeliveryItemDTO[];
}

export const fairDeliveryService = {
  create: async (delivery: FairDeliveryDTO): Promise<FairDeliveryDTO> => {
    const response = await apiClient.post<FairDeliveryDTO>('/fair-deliveries', delivery);
    return response.data;
  },

  update: async (id: string, delivery: FairDeliveryDTO): Promise<FairDeliveryDTO> => {
    const response = await apiClient.put<FairDeliveryDTO>(`/fair-deliveries/${id}`, delivery);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/fair-deliveries/${id}`);
  },

  updateReturn: async (id: string, returnedItems: FairDeliveryItemDTO[]): Promise<FairDeliveryDTO> => {
    const response = await apiClient.patch<FairDeliveryDTO>(`/fair-deliveries/${id}/return`, returnedItems);
    return response.data;
  },

  getProfit: async (id: string): Promise<number> => {
    const response = await apiClient.get<number>(`/fair-deliveries/${id}/profit`);
    return response.data;
  },

  get: async (id: string): Promise<FairDeliveryDTO> => {
    const response = await apiClient.get<FairDeliveryDTO>(`/fair-deliveries/${id}`);
    return response.data;
  },

  list: async (): Promise<FairDeliveryDTO[]> => {
    const response = await apiClient.get<FairDeliveryDTO[]>('/fair-deliveries');
    return response.data;
  },
};
