package com.example.KodikaraGroupBusinessManagementApplication.model;
import com.example.KodikaraGroupBusinessManagementApplication.model.ShopSupplyItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name="shop_supply")
@NoArgsConstructor
@AllArgsConstructor
public class ShopSupply {
    @Id
    @Column(name="supply_id",columnDefinition = "CHAR(10)")
    private String supplyId;

    @ManyToOne
    @JoinColumn(name="vehicle_id",nullable = false)
    private Vehicle vehicle;
    @ManyToOne
    @JoinColumn(name="salesman_user_id",nullable = false)
    private User salesman;
    @ManyToOne
    @JoinColumn(name="driver_id",nullable = false)
    private Driver driver;
    @Column(name = "supply_date",nullable = false)
    private LocalDate supplyDate;
    @OneToMany(mappedBy = "shopSupply", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected List<ShopSupplyItem> items;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private java.math.BigDecimal paidAmount = java.math.BigDecimal.ZERO;

    @Column(name = "outstanding_amount", precision = 10, scale = 2)
    private java.math.BigDecimal outstandingAmount = java.math.BigDecimal.ZERO;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "UNCOMPLETED";

}
