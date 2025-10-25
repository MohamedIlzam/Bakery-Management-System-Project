package com.example.KodikaraGroupBusinessManagementApplication.Controller;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.*;
import com.example.KodikaraGroupBusinessManagementApplication.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/daily")
    public ResponseEntity<DailyReportDTO> generateDailyReportForDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyReportDTO report = reportService.generateDaily(date);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyReportDTO>> getDailyReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<DailyReportDTO> reports = reportService.getReportByDate(date);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/daily/{id}")
    public ResponseEntity<DailyReportDTO> getDailyReport(@PathVariable String id) {
        DailyReportDTO report = reportService.getDailyReportById(id);
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/daily/{id}")
    public ResponseEntity<Void> deleteDailyReport(@PathVariable String id) {
        reportService.deleteByDate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/monthly")
    public ResponseEntity<MonthlyReportDTO> generateMonthlyReportForMonth(
            @RequestParam String yearMonth) {

        YearMonth ym = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        MonthlyReportDTO report = reportService.generateMonthly(ym);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyReportDTO>> getMonthlyReports(
            @RequestParam String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        List<MonthlyReportDTO> reports = reportService.getMonthlyReportByMonth(ym);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/monthly/{id}")
    public ResponseEntity<MonthlyReportDTO> getMonthlyReport(@PathVariable String id) {
        MonthlyReportDTO report = reportService.getMonthlyReportById(id);
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/monthly/{id}")
    public ResponseEntity<Void> deleteMonthlyReport(@PathVariable String id) {
        reportService.deleteMonthlyReportById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics/sales-data")
    public ResponseEntity<List<SaleResponseDTO>> getFilteredSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Optional<String> vehicleNo,
            @RequestParam(required = false) Optional<String> shopName,
            @RequestParam(required = false) Optional<String> driverName) {

        List<SaleResponseDTO> data = reportService.getFilterSaleData(startDate, endDate, vehicleNo, shopName, driverName);
        return ResponseEntity.ok(data);
    }
}