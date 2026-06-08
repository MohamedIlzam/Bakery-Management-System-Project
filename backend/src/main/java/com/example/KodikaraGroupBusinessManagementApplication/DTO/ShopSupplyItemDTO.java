package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopSupplyItemDTO {
    private String productId;
    private String productName;
    private String shopId;
    private BigDecimal price;
    private int quantity;
    private int returnQuantity;
    private int expiredQuantity;
}
