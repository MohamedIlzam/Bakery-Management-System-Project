package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MonthlyReportDTO {
    private String mreportId;
    private String reportMonth;
    private BigDecimal mtotalSales;
    private int mtotalTransactions;
    private LocalDateTime generatedOn;

}
