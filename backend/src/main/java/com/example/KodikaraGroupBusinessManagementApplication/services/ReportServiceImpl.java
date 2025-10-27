package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.*;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.*;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.*;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final DailyReportRepository dailyReportRepository;
    private final MonthlyReportRepository monthlyReportRepository;
    private final SaleRepository saleRepository;
    private final SaleService saleService; // Inject SaleService to use its helpers

    // --- Daily Report Implementation ---

    @Override
    public DailyReportDTO generateDailyReport(LocalDate date) {
        if (dailyReportRepository.existsByDreportDate(date)) {
            throw new IllegalStateException("Daily report already exists for date: " + date);
        }

        List<Sale> salesForDate = saleRepository.findBySaleDate(date);

        BigDecimal totalSalesAmount = salesForDate.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int numberOfTransactions = salesForDate.size();

        DailyReport newReport = new DailyReport();
        newReport.setDreportId(IdGenerator.dailyReportId());
        newReport.setDreportDate(date);
        newReport.setDtotalSales(totalSalesAmount);
        newReport.setDtotalTransac(numberOfTransactions);
        // generatedOn is set by @CreationTimestamp

        DailyReport savedReport = dailyReportRepository.save(newReport);
        return convertToDailyDTO(savedReport);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyReportDTO> getDailyReportsByDate(LocalDate date) {
        List<DailyReport> reports = dailyReportRepository.findByDreportDate(date);
        return reports.stream().map(this::convertToDailyDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DailyReportDTO getDailyReportById(String reportId) {
        DailyReport report = dailyReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Daily report not found with id: " + reportId));
        return convertToDailyDTO(report);
    }

    @Override
    public void deleteDailyReport(String reportId) {
        if (!dailyReportRepository.existsById(reportId)) {
            throw new ResourceNotFoundException("Daily report not found with id: " + reportId);
        }
        dailyReportRepository.deleteById(reportId);
    }

    // --- Monthly Report Implementation ---

    @Override
    public MonthlyReportDTO generateMonthlyReport(YearMonth yearMonth) {
        String monthString = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        if (monthlyReportRepository.existsByMreportDate(monthString)) {
            throw new IllegalStateException("Monthly report already exists for month: " + monthString);
        }

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<Sale> salesForMonth = saleRepository.findBySaleDateBetween(startDate, endDate);

        BigDecimal totalSalesAmount = salesForMonth.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int numberOfTransactions = salesForMonth.size();

        MonthlyReport newReport = new MonthlyReport();
        newReport.setMreportId(IdGenerator.monthlyReportId());
        newReport.setMreportDate(monthString);
        newReport.setMtotalSales(totalSalesAmount); // Use correct setter
        newReport.setMtotalTransac(numberOfTransactions); // Use correct setter
        // generatedOn is set by @CreationTimestamp

        MonthlyReport savedReport = monthlyReportRepository.save(newReport);
        return convertToMonthlyDTO(savedReport);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyReportDTO> getMonthlyReportsByMonth(YearMonth yearMonth) {
        String monthString = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<MonthlyReport> reports = monthlyReportRepository.findByMreportDate(monthString); // Use correct param type
        return reports.stream().map(this::convertToMonthlyDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyReportDTO getMonthlyReportById(String reportId) {
        MonthlyReport report = monthlyReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Monthly report not found with id: " + reportId));
        return convertToMonthlyDTO(report);
    }

    @Override
    public void deleteMonthlyReport(String reportId) {
        if (!monthlyReportRepository.existsById(reportId)) {
            throw new ResourceNotFoundException("Monthly report not found with id: " + reportId);
        }
        monthlyReportRepository.deleteById(reportId);
    }

    // --- Analytics Part Implementation ---

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getFilteredSalesData(LocalDate startDate, LocalDate endDate,
                                                      Optional<String> vehicleNo,
                                                      Optional<String> shopName,
                                                      Optional<String> driverName) {

        // Call the new repository method.
        // The .orElse(null) part passes 'null' to the query if the Optional is empty,
        // which tells the query to skip that filter.
        List<Sale> filteredSales = saleRepository.findSalesByCriteria(
                startDate,
                endDate,
                vehicleNo.orElse(null),
                shopName.orElse(null),
                driverName.orElse(null)
        );

        // Use the public helper method from SaleService (this part stays the same)
        return saleService.convertToResponseDTOList(filteredSales);
    }

    // --- Helper DTO Conversion Methods ---

    private DailyReportDTO convertToDailyDTO(DailyReport report) {
        // Use the corrected DailyReportDTO
        return new DailyReportDTO(
                report.getDreportId(),
                report.getDreportDate(),
                report.getDtotalSales(),
                report.getDtotalTransac(),
                report.getGeneratedOn()
        );
    }

    private MonthlyReportDTO convertToMonthlyDTO(MonthlyReport report) {
        // Use the corrected MonthlyReportDTO and entity getters
        return new MonthlyReportDTO(
                report.getMreportId(),
                report.getMreportDate(),
                report.getMtotalSales(), // Use correct getter
                report.getMtotalTransac(), // Use correct getter
                report.getGeneratedOn() // Use correct getter
        );
    }
}