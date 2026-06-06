package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportDTO {
    private String mreportId;
    private String reportMonth;
    private BigDecimal mtotalSales;
    private int mtotalTransac;
    private LocalDateTime generatedOn;
}