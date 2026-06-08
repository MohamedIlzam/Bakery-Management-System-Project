package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FairDeliveryItemDTO {

    private String itemId;
    private String deliveryId;
    private String productId;

    private int qtySent;
    private int qtyRemaining;
    private int qtyExpired;

    private BigDecimal unitPrice = BigDecimal.ZERO;
}
