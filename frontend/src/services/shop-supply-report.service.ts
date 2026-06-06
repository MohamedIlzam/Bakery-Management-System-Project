import apiClient from '@/lib/api-client';

export interface ShopSupplyReportDTO {
  reportId?: string;
  sreportDate?: string;
  reportMonth?: string;
  reportType: 'DAILY' | 'MONTHLY';
  totalSupplies?: number;
  totalAmount?: number;
  totalShopsServed?: number;
  generatedOn?: string;
}

export const shopSupplyReportService = {
  /**
   * Generate a daily shop supply report
   */
  generateDaily: async (date: string): Promise<ShopSupplyReportDTO> => {
    const response = await apiClient.post<ShopSupplyReportDTO>(
      `/reports/shop-supply/daily?date=${date}`);
    return response.data;
  },

  /**
   * Generate a monthly shop supply report
   */
  generateMonthly: async (month: string): Promise<ShopSupplyReportDTO> => {
    const response = await apiClient.post<ShopSupplyReportDTO>(
      `/reports/shop-supply/monthly?yearMonth=${month}`
    );
    return response.data;
  },

  /**
   * Get all shop supply reports
   */
  list: async (): Promise<ShopSupplyReportDTO[]> => {
    const response = await apiClient.get<ShopSupplyReportDTO[]>('/reports/shop-supply');
    return response.data;
  },

  /**
   * Get reports by date
   */
  getByDate: async (date: string): Promise<ShopSupplyReportDTO[]> => {
      const allReports = await shopSupplyReportService.list();
      return allReports.filter(report => report.sreportDate === date);
    },

  /**
   * Get reports by month
   */
  getByMonth: async (month: string): Promise<ShopSupplyReportDTO[]> => {
      const allReports = await shopSupplyReportService.list();
      // Assuming reportMonth is "YYYY-MM"
      return allReports.filter(report => report.reportMonth === month);
    },

  /**
   * Get a specific report by ID
   */
  get: async (id: string): Promise<ShopSupplyReportDTO> => {
    const response = await apiClient.get<ShopSupplyReportDTO>(`/reports/shop-supply/${id}`);
    return response.data;
  },

  /**
   * Delete a report
   */
  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/reports/shop-supply/${id}`);
  },
};

