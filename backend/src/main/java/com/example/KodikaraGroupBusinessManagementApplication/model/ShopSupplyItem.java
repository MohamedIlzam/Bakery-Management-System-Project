package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_supply_item")
public class ShopSupplyItem {
    @Id
    @Column(name = "sitem_id", columnDefinition = "CHAR(10)")
    private String itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ShopSupply shopSupply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pro_id", nullable = false)
    private Product product;

    @Column(name = "qty_supplied", nullable = false)
    private int qtySupplied;
    @Column(name = "qty_returned", nullable = false)
    private int qtyReturned = 0;
    @Column(name = "qty_expired", nullable = false)
    private int qtyExpired = 0;
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;
}
