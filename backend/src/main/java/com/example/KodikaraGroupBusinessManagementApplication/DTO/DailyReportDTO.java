package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DailyReportDTO {
    private String drepotID;
  private LocalDate reportDate;
  private BigDecimal totalslSales;
  private int totTransactions;
  private LocalDateTime generatedOn;

}
