package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.FairDeliveryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FairDeliveryReportRepository extends JpaRepository<FairDeliveryReport, String> {
    List<FairDeliveryReport> findByFreportDate(LocalDate reportDate);
    List<FairDeliveryReport> findByreportMonth(String reportMonth);
    List<FairDeliveryReport> findAllByreportMonthAndReportType(String reportMonth, String reportType);

    Optional<FairDeliveryReport> findByFreportDateAndReportType(LocalDate reportDate, String reportType);
    Optional<FairDeliveryReport> findByReportMonthAndReportType(String reportMonth, String reportType);

}
