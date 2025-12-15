package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.ShopSupplyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShopSupplyReportRepository extends JpaRepository<ShopSupplyReport,String> {
    List<ShopSupplyReport> findBySreportDate(LocalDate sreportDate);
    List<ShopSupplyReport> findByReportMonth(String reportMonth);
    List<ShopSupplyReport> findAllByReportMonthAndReportType(String reportmonth, String reportType);


    Optional<ShopSupplyReport> findBySreportDateAndReportType(LocalDate sreportDate,String reportType);
    Optional<ShopSupplyReport> findByReportMonthAndReportType(String reportmonth,String reportType);
}
