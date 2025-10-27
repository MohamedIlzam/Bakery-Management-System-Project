package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReportDTO {
    private String dreportId; // Corrected spelling
    private LocalDate reportDate;
    private BigDecimal totalSales; // Corrected spelling
    private int totTransactions;
    private LocalDateTime generatedOn;
}