package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockReportDTO {
    private String productId;
    private String productName;
    private String shopId;
    private String shopName;
    private LocalDate date;
    private Integer morningQuantity;
    private Integer closingQuantity;
    private Integer stockSold;
    private BigDecimal income;
    private Integer remainingStock;
}