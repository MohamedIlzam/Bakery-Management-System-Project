import apiClient from '@/lib/api-client';
import { SaleResponseDTO } from './sale.service';

export interface DailyReportDetailDTO {
  productName: string;
  quantitySold: number;
  totalRevenue: number;
}

export interface DailyReportDTO {
  reportId: string;
  date: string;
  totalSales: number;
  totalRevenue: number;
  details: DailyReportDetailDTO[];
}

export interface MonthlyReportDTO {
  reportId: string;
  yearMonth: string;
  totalSales: number;
  totalRevenue: number;
  totalExpenses: number;
  netProfit: number;
}

export const reportService = {
  // Daily Reports
  generateDailyReport: async (date: string): Promise<DailyReportDTO> => {
    const response = await apiClient.post<DailyReportDTO>('/reports/daily', null, {
      params: { date },
    });
    return response.data;
  },

  getDailyReports: async (date: string): Promise<DailyReportDTO[]> => {
    const response = await apiClient.get<DailyReportDTO[]>('/reports/daily', {
      params: { date },
    });
    return response.data;
  },

  getDailyReportById: async (id: string): Promise<DailyReportDTO> => {
    const response = await apiClient.get<DailyReportDTO>(`/reports/daily/${id}`);
    return response.data;
  },

  deleteDailyReport: async (id: string): Promise<void> => {
    await apiClient.delete(`/reports/daily/${id}`);
  },

  // Monthly Reports
  generateMonthlyReport: async (yearMonth: string): Promise<MonthlyReportDTO> => {
    const response = await apiClient.post<MonthlyReportDTO>('/reports/monthly', null, {
      params: { yearMonth },
    });
    return response.data;
  },

  getMonthlyReports: async (yearMonth: string): Promise<MonthlyReportDTO[]> => {
    const response = await apiClient.get<MonthlyReportDTO[]>('/reports/monthly', {
      params: { yearMonth },
    });
    return response.data;
  },

  getMonthlyReportById: async (id: string): Promise<MonthlyReportDTO> => {
    const response = await apiClient.get<MonthlyReportDTO>(`/reports/monthly/${id}`);
    return response.data;
  },

  deleteMonthlyReport: async (id: string): Promise<void> => {
    await apiClient.delete(`/reports/monthly/${id}`);
  },

  // Analytics
  getFilteredSalesData: async (
    startDate: string,
    endDate: string,
    vehicleNo?: string,
    shopName?: string,
    driverName?: string
  ): Promise<SaleResponseDTO[]> => {
    const response = await apiClient.get<SaleResponseDTO[]>('/reports/analytics/sales-data', {
      params: {
        startDate,
        endDate,
        vehicleNo,
        shopName,
        driverName,
      },
    });
    return response.data;
  },
};

