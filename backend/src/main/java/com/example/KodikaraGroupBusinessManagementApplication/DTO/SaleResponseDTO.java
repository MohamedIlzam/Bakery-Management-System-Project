package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseDTO {
    private String saleId;
    private String shopName;
    private String ownerName;
    private String contactNo;
    private String driverName;
    private String vehicleNo;
    private List<SaleItemResponse> items;
    private BigDecimal totalAmount;
    private LocalDate saleDate;
}