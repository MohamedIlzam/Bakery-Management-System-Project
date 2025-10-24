package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyReportDTO {
    private Integer year;
    private Integer month;
    private Double totalIncome;
    private Long totalSales;

}
