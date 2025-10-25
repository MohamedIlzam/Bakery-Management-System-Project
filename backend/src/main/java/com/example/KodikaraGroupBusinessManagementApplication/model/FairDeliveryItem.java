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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fitem_id")
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private FairDelivery fairDelivery;

    @ManyToOne
    @JoinColumn(name = "pro_id")
    private Product product;

    @Column(name = "qty_sent")
    private int qtySent;

    @Column(name = "qty_remaining")
    private int qtyRemaining;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;
}
