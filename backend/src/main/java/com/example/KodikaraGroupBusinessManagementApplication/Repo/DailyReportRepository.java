package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport,String> {
      List<DailyReport> findByDreportDate(LocalDate dreportDate);
      boolean existsByDreportDate(LocalDate dreportDate);
}
