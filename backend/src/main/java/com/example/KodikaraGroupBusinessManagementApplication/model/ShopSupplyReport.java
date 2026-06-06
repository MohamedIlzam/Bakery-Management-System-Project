package com.example.KodikaraGroupBusinessManagementApplication.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name="shop_supply_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopSupplyReport {
    @Id
    @Column(name="sreport_id",nullable = false,columnDefinition = "CHAR(10)")
    private String sreportId;
    @Column(name="report_date",nullable = false)
    private LocalDate sreportDate;
    @Column(name="report_month",columnDefinition = "CHAR(7)")
    private String reportMonth;
    @Column(name="report_type",columnDefinition = "VARCHAR(10)")
    private String reportType;
    @Column(name="total_supplies")
    private Integer totalSupplies;
    @Column(name="total_amount",precision = 12,scale = 2)
    private BigDecimal totalAmount;
    @Column(name="total_shops_served")
    private Integer totalShopsServed;
    @CreationTimestamp
    @Column(name="generated_on",nullable = false)//,updatable = false)
    private LocalDateTime generatedOn;


}
