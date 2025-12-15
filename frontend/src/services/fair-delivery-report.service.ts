import apiClient from '@/lib/api-client';

export interface FairDeliveryReportDTO {
  reportId: string;
  freportDate?: string;
  reportMonth?: string;
  reportType: 'DAILY' | 'MONTHLY';
  totalDeliveries: number;
  totalRevenue: number;
  totalProfit: number;
  totalExpences: number;
  generatedOn: string;
}

export const fairDeliveryReportService = {
  // Generate daily report
  generateDaily: async (date: string): Promise<FairDeliveryReportDTO> => {
    const response = await apiClient.post<FairDeliveryReportDTO>(`/reports/fair-delivery/daily?date=${date}`);
    return response.data;
  },

  // Generate monthly report
  generateMonthly: async (month: string): Promise<FairDeliveryReportDTO> => {
    const response = await apiClient.post<FairDeliveryReportDTO>(`/reports/fair-delivery/monthly?yearMonth=${month}`);
    return response.data;
  },

  // Get all reports
  list: async (): Promise<FairDeliveryReportDTO[]> => {
    const response = await apiClient.get<FairDeliveryReportDTO[]>('/reports/fair-delivery'); 
    return response.data;
  },

  // // Get reports by date
  // getByDate: async (date: string): Promise<FairDeliveryReportDTO[]> => {
  //   const response = await apiClient.get<FairDeliveryReportDTO[]>(`/fair-delivery-reports/by-date?date=${date}`);
  //   return response.data;
  // },
  getByDate: async (date: string): Promise<FairDeliveryReportDTO[]> => {
    const allReports = await fairDeliveryReportService.list();
    return allReports.filter(report => report.freportDate === date);
  },

  // // Get reports by month
  // getByMonth: async (month: string): Promise<FairDeliveryReportDTO[]> => {
  //   const response = await apiClient.get<FairDeliveryReportDTO[]>(`/fair-delivery-reports/by-month?month=${month}`);
  //   return response.data;
  // },
  getByMonth: async (month: string): Promise<FairDeliveryReportDTO[]> => {
    const allReports = await fairDeliveryReportService.list();
    // Assuming reportMonth is "YYYY-MM"
    return allReports.filter(report => report.reportMonth === month);
  },

  // Get single report
  get: async (id: string): Promise<FairDeliveryReportDTO> => {
    const response = await apiClient.get<FairDeliveryReportDTO>(`/reports/fair-delivery/${id}`);
    return response.data;
  },

  // Delete report
  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/reports/fair-delivery/${id}`);
  },
};

