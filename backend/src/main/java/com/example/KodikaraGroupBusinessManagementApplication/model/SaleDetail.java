package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_detail")
@Getter // Use Lombok
@Setter // Use Lombok
@NoArgsConstructor
@AllArgsConstructor
public class SaleDetail {
    @Id
    @Column(name="sdetail_id", columnDefinition = "CHAR(10)")
    private String sdetailId;

    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @ManyToOne
    @JoinColumn(name="pro_id", nullable = false)
    private Product product;

    @Column(name="qty", nullable = false)
    private int qty;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "subtot", precision = 10, scale = 2, nullable = false)
    private BigDecimal subTot;
}