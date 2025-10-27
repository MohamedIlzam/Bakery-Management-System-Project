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

    // === Daily Report CRUD ===

    @PostMapping("/daily")
    public ResponseEntity<DailyReportDTO> generateDailyReportForDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // Use correct service method name
        DailyReportDTO report = reportService.generateDailyReport(date);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyReportDTO>> getDailyReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // Use correct service method name
        List<DailyReportDTO> reports = reportService.getDailyReportsByDate(date);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/daily/{id}")
    public ResponseEntity<DailyReportDTO> getDailyReportById(@PathVariable String id) {
        DailyReportDTO report = reportService.getDailyReportById(id);
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/daily/{id}")
    public ResponseEntity<Void> deleteDailyReport(@PathVariable String id) {
        reportService.deleteDailyReport(id); // Use correct service method name
        return ResponseEntity.noContent().build();
    }

    // === Monthly Report CRUD ===

    @PostMapping("/monthly")
    public ResponseEntity<MonthlyReportDTO> generateMonthlyReportForMonth(
            @RequestParam String yearMonth) { // Expecting "YYYY-MM"
        YearMonth ym = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        // Use correct service method name
        MonthlyReportDTO report = reportService.generateMonthlyReport(ym);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyReportDTO>> getMonthlyReportsByMonth(
            @RequestParam String yearMonth) { // Expecting "YYYY-MM"
        YearMonth ym = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        // Use correct service method name
        List<MonthlyReportDTO> reports = reportService.getMonthlyReportsByMonth(ym);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/monthly/{id}")
    public ResponseEntity<MonthlyReportDTO> getMonthlyReportById(@PathVariable String id) {
        MonthlyReportDTO report = reportService.getMonthlyReportById(id);
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/monthly/{id}")
    public ResponseEntity<Void> deleteMonthlyReport(@PathVariable String id) {
        reportService.deleteMonthlyReport(id); // Use correct service method name
        return ResponseEntity.noContent().build();
    }

    // === Analytics Endpoint ===

    @GetMapping("/analytics/sales-data")
    public ResponseEntity<List<SaleResponseDTO>> getFilteredSalesData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Optional<String> vehicleNo,
            @RequestParam(required = false) Optional<String> shopName,
            @RequestParam(required = false) Optional<String> driverName) {

        List<SaleResponseDTO> data = reportService.getFilteredSalesData(startDate, endDate, vehicleNo, shopName, driverName);
        return ResponseEntity.ok(data);
    }
}