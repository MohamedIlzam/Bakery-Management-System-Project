package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopSupplyDTO {
    private String supplyId;
    private String vehicleId;
    private String salesmanId; // Who entered the data (User)
    private String driverId; // Who drove the vehicle
    private String shopId;
    private LocalDate supplyDate;
    private List<ShopSupplyItemDTO> items;

    // Display fields
    private String shopName;
    private String salesmanName; // Salesman's username
    private String driverName; // Driver's name
    private String vehicleNo;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal outstandingAmount;
    private String paymentStatus;
}