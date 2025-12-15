package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.DailyReportDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.MonthlyReportDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.SaleResponseDTO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface ReportService {
    //  Daily Report CRUD
    DailyReportDTO generateDailyReport(LocalDate date);
    List<DailyReportDTO> getDailyReportsByDate(LocalDate date);
    DailyReportDTO getDailyReportById(String reportId);
    void deleteDailyReport(String reportId);

    //  Monthly Report CRUD
    MonthlyReportDTO generateMonthlyReport(YearMonth yearMonth);
    List<MonthlyReportDTO> getMonthlyReportsByMonth(YearMonth yearMonth);
    MonthlyReportDTO getMonthlyReportById(String reportId);
    void deleteMonthlyReport(String reportId);

    // Analytics Part (Read Filtered Sales Data)
    List<SaleResponseDTO> getFilteredSalesData(LocalDate startDate, LocalDate endDate,
                                               Optional<String> vehicleNo,
                                               Optional<String> shopName,
                                               Optional<String> driverName);
}