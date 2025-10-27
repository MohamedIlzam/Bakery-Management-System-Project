package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// import java.time.LocalDate; // No longer needed
import java.util.List;

@Repository
public interface MonthlyReportRepository extends JpaRepository<MonthlyReport,String> {
    // Parameter changed to String to match 'mreportDate' field
    List<MonthlyReport> findByMreportDate(String yearMonth);

    // Field name corrected to 'mreportDate'
    boolean existsByMreportDate(String yearMonth);
}