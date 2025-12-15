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
@Table(name="fair_delivery_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FairDeliveryReport {
    @Id
    @Column(name="freport_id",columnDefinition = "CHAR(10)")
    private String freportId;
    @Column(name="report_date",nullable = false)
    private LocalDate freportDate;
    @Column(name="report_month",columnDefinition = "CHAR(7)")
    private String reportMonth;
    @Column(name="report_type",columnDefinition = "VARCHAR(10)")
    private String reportType;
    @Column(name="total_deliveries")
    private Integer totalDeliveries;
    @Column(name="total_revenue",precision = 12,scale = 2)
    private BigDecimal totalRevenue;
    @Column(name="total_profit",precision = 12,scale = 2)
    private BigDecimal totalProfit;
    @Column(name="total_expenses",precision = 12,scale = 2)
    private BigDecimal totalExpenses;
    @CreationTimestamp
    @Column(name="generated_on",nullable = false)//,updatable = false)
    private LocalDateTime generatedOn;
}
//report_id CHAR(10) NOT NULL,
//report_date DATE,
//report_month CHAR(7),
//report_type VARCHAR(10) NOT NULL,
//total_deliveries INT,
//total_revenue DECIMAL(12,2),
//total_profit DECIMAL(12,2),
//total_expenses DECIMAL(12,2),
//generated_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
//PRIMARY KEY (report_id)
