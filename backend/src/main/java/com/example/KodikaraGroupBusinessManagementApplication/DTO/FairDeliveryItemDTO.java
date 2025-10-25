package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FairDeliveryItemDTO {

    private Long itemId;
    private Long deliveryId;
    private Long productId;

    private int qtySent;
    private int qtyRemaining;

    private BigDecimal unitPrice = BigDecimal.ZERO;
}
