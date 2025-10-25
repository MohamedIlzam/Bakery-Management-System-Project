package com.example.KodikaraGroupBusinessManagementApplication.Repo;


import com.example.KodikaraGroupBusinessManagementApplication.model.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MonthlyReportRepository extends JpaRepository<MonthlyReport,String> {
   List<MonthlyReport> findByMreportDate(String yearMonth);
   boolean existsByMreportDate(String yearMonth);

}
