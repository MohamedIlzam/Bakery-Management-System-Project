package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleDTO {

    private String productName;
    private int quantity;
    private BigDecimal price;
}