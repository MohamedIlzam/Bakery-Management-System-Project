package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sale")
@NoArgsConstructor
public class Sale {
    @Id
    @Column(name = "sale_id", columnDefinition = "CHAR(10)")
    private String saleId;

    @ManyToOne
    @JoinColumn(name= "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name ="vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="driver_id")
    private Driver driver;

    @Column(name="sale_date", nullable = false)
    private LocalDate saleDate;

    @Column(name="tot_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "payment_method", length = 20, nullable = false)
    private String paymentMethod;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleDetail> saleDetails = new ArrayList<>();
}