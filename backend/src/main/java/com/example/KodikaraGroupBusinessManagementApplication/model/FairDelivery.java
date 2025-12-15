package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fair_delivery")
public class FairDelivery {

    @Id
    @Column(name = "delivery_id", columnDefinition = "CHAR(10)")
    private String deliveryId;

    @Column(name = "fair_name", nullable = false, length=100)
    private String fairName;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Column(name = "extra_payments", precision = 15, scale = 2)
    private BigDecimal extraPayments = BigDecimal.ZERO;

    @Column(name = "tax", precision = 15, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "diesel_amount", precision = 15, scale = 2)
    private BigDecimal dieselAmount = BigDecimal.ZERO;

    @Column(name = "profit", precision = 15, scale = 2)
    private BigDecimal profit = BigDecimal.ZERO;

    @Column(name = "dstatus", length = 20)
    private String status; // OUT or RETURNED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @OneToMany(mappedBy = "fairDelivery", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FairDeliveryItem> items = new ArrayList<>();
}