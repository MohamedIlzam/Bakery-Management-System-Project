package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleItemDTO {
    private String productName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
