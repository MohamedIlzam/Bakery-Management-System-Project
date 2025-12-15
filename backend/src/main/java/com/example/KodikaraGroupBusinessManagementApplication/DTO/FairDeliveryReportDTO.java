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
public class FairDeliveryReportDTO {
    private String freportID;
    private LocalDate freportDate;
    private String reportMonth;
    private String reportType;
    private int totalDeliveries;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private BigDecimal totalExpences;
    private LocalDateTime generatedOn;
}
