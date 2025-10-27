package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor // Add NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportDTO {
    private String mreportId;
    private String reportMonth; // e.g., "2025-10"
    private BigDecimal mtotalSales; // Matched entity field
    private int mtotalTransac; // Matched entity field
    private LocalDateTime generatedOn;
}