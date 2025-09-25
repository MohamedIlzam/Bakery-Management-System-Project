package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyReportDTO {
  private LocalDate localDate;
  private String cutomerName;
  private Double totalIncome;
  private Long totalProductsSold;

}
