package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fair_delivery_item")
public class FairDeliveryItem {

    @Id
    @Column(name = "fitem_id", columnDefinition = "CHAR(10)")
    private String itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private FairDelivery fairDelivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pro_id", nullable = false)
    private Product product;

    @Column(name = "qty_sent", nullable = false)
    private int qtySent;

    @Column(name = "qty_remaining")
    private int qtyRemaining;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;
}