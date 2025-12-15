import apiClient from '@/lib/api-client';

export interface ProductDTO {
  proId?: string;         // Matches backend DTO (product ID)
  productId?: string;     // Alias for proId (for compatibility)
  name: string;           // Matches backend DTO (product name)
  productName?: string;   // Alias for name (for compatibility)
  category?: string;      // Matches backend DTO
  unitPrice: number;      // Matches backend DTO (BigDecimal)
  price?: number;         // Alias for unitPrice (for compatibility)
}

export const productService = {
  create: async (product: ProductDTO): Promise<ProductDTO> => {
    const response = await apiClient.post<ProductDTO>('/products', product);
    return response.data;
  },

  update: async (id: string, product: ProductDTO): Promise<ProductDTO> => {
    const response = await apiClient.put<ProductDTO>(`/products/${id}`, product);
    return response.data;
  },

  get: async (id: string): Promise<ProductDTO> => {
    const response = await apiClient.get<ProductDTO>(`/products/${id}`);
    return response.data;
  },

  list: async (): Promise<ProductDTO[]> => {
    const response = await apiClient.get<ProductDTO[]>('/products');
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/products/${id}`);
  },
};
