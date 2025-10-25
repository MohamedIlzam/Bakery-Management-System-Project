package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FairDeliveryDTO {

    private Long deliveryId;
    private String fairName;
    private LocalDate deliveryDate;

    private BigDecimal extraPayments = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;
    private BigDecimal dieselAmount = BigDecimal.ZERO;
    private BigDecimal profit = BigDecimal.ZERO;

    private String status; // OUT or RETURNED

    private Long vehicleId;
    private Long driverId;

    private List<FairDeliveryItemDTO> items;
}
