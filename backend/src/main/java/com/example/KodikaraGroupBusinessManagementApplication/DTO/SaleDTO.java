package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleDTO {
    private String ProductName;
    private int Quantity;
    private BigDecimal Price;

}
