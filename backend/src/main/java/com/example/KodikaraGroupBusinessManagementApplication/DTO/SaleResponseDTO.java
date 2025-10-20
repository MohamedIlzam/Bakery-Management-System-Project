package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseDTO {
    private String saleId;
    private String shopName;
    private String driverName;
    private String vehicleNo;
    private List<SaleItemResponse> items;

    public SaleResponseDTO(String productName, int quantity, BigDecimal subtotal) {
    }

    public SaleResponseDTO(String saleId, String shopName, String ownerName, String contactNo, String driverName, String vehicleNo, BigDecimal totalAmount, Object saleDate) {
    }

    public SaleResponseDTO(String saleId, String shopName, String ownerName, String contactNo, String vehicleNo, String driverName, List<SaleItemResponse> items, BigDecimal totalAmount, Object saleDate) {
    }
}
