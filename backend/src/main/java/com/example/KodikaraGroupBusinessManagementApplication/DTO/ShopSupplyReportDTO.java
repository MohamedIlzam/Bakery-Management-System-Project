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
public class ShopSupplyReportDTO {
    private String sreportId;
    private LocalDate sreportDate;
    private String reportMonth;
    private String reportType;
    private Integer totalSupplies;
    private BigDecimal totalAmount;
    private Integer totalShopsServed;
    private LocalDateTime generatedOn;
}