package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.DailyReportDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.MonthlyReportDTO;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.SaleResponseDTO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface ReportService {
    DailyReportDTO generateDaily(LocalDate date);

    DailyReportDTO getDailyReportById(String dreportId);

    List<DailyReportDTO> getReportByDate(LocalDate date);

    void deleteByDate(String dreportId);

    MonthlyReportDTO generateMonthly(YearMonth yearMonth);

    MonthlyReportDTO getMonthlyReportById(String mreportid);

    List<MonthlyReportDTO> getMonthlyReportByMonth(YearMonth yearMonth);

    void deleteMonthlyReportById(String mreportid);
    List<SaleResponseDTO> getFilterSaleData(LocalDate startDate,LocalDate endDate,
                                            Optional<String> vehicleNo,
                                            Optional<String> shopName,
                                            Optional<String> driverName);
}