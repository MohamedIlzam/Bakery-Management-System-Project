package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockEntryDTO {
    private String productId;
    private Integer shopId;
    private LocalDate date;
    private Integer morningQuantity;
    private Integer closingQuantity;
}
