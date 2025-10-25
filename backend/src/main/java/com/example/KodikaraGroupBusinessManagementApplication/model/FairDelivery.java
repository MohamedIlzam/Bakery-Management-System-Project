package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fair_delivery")
public class FairDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;

    @Column(name = "fair_name")
    private String fairName;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "extra_payments", precision = 15, scale = 2)
    private BigDecimal extraPayments = BigDecimal.ZERO;

    @Column(name = "tax", precision = 15, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "diesel_amount", precision = 15, scale = 2)
    private BigDecimal dieselAmount = BigDecimal.ZERO;

    @Column(name = "profit", precision = 15, scale = 2)
    private BigDecimal profit = BigDecimal.ZERO;

    @Column(name = "dstatus")
    private String status; // OUT or RETURNED

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @OneToMany(mappedBy = "fairDelivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FairDeliveryItem> items;
}
