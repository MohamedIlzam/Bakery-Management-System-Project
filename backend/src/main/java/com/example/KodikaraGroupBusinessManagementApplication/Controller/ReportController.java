package com.example.KodikaraGroupBusinessManagementApplication.Controller;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.*;
import com.example.KodikaraGroupBusinessManagementApplication.model.FairDeliveryReport;
import com.example.KodikaraGroupBusinessManagementApplication.services.FairDeliveryReportService;
import com.example.KodikaraGroupBusinessManagementApplication.services.ReportService;
import com.example.KodikaraGroupBusinessManagementApplication.services.ShopSupplyReportService;
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
    private final FairDeliveryReportService fairDeliveryReportService;
    private final ShopSupplyReportService shopSupplyReportService;


    @GetMapping("/fair-delivery")
    public ResponseEntity<List<FairDeliveryReportDTO>> getAllFairDeliveryReports() {
        return ResponseEntity.ok(fairDeliveryReportService.getAllReports());
    }
    @GetMapping("/shop-supply")
    public ResponseEntity<List<ShopSupplyReportDTO>> getAllShopSupplyReports() {
        return ResponseEntity.ok(shopSupplyReportService.getAllReports());
    }
    //Daily Report CRUD

    @PostMapping("/daily")
    public ResponseEntity<DailyReportDTO> generateDailyReportForDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyReportDTO report = reportService.generateDailyReport(date);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }
    @PostMapping("/fair-delivery/daily")
    public ResponseEntity<FairDeliveryReportDTO> generateFairDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        FairDeliveryReportDTO freport = fairDeliveryReportService.generateDailyReport(date);
        return ResponseEntity.status(HttpStatus.CREATED).body(freport);
    }
    @PostMapping("/shop-supply/daily")
    public ResponseEntity<ShopSupplyReportDTO> generateShopDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ShopSupplyReportDTO sreport = shopSupplyReportService.generateDailyReport(date);
        return ResponseEntity.status(HttpStatus.CREATED).body(sreport);
    }
    @GetMapping("/daily")
    public ResponseEntity<List<DailyReportDTO>> getDailyReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
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
        reportService.deleteDailyReport(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/fair-delivery/{id}")
    public ResponseEntity<Void> deleteFairReport(@PathVariable String id) {
        fairDeliveryReportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/shop-supply/{id}")
    public ResponseEntity<Void> deleteShopReport(@PathVariable String id) {
        shopSupplyReportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    //Monthly Report CRUD

    @PostMapping("/monthly")
    public ResponseEntity<MonthlyReportDTO> generateMonthlyReportForMonth(
            @RequestParam String yearMonth) { // Expect "YYYY-MM"
        YearMonth ym = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        MonthlyReportDTO report = reportService.generateMonthlyReport(ym);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }
    @PostMapping("/shop-supply/monthly")
    public ResponseEntity<ShopSupplyReportDTO> generateShopMonthlyReport(
            @RequestParam String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        ShopSupplyReportDTO smreport = shopSupplyReportService.generateMonthlyReport(ym);
        return ResponseEntity.status(HttpStatus.CREATED).body(smreport);
    }
    @PostMapping("/fair-delivery/monthly")
    public ResponseEntity<FairDeliveryReportDTO> generateFairMonthlyReport(
            @RequestParam String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        FairDeliveryReportDTO fmreport = fairDeliveryReportService.generateMonthlyReport(ym);
        return ResponseEntity.status(HttpStatus.CREATED).body(fmreport);
    }
    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyReportDTO>> getMonthlyReportsByMonth(
            @RequestParam String yearMonth) { // Expect "YYYY-MM"
        YearMonth ym = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
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
        reportService.deleteMonthlyReport(id);
        return ResponseEntity.noContent().build();
    }

    //Analytics Endpoint

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