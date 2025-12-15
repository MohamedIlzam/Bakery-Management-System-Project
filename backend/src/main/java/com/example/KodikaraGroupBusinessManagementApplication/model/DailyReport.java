package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name="daily_report")
@NoArgsConstructor
@AllArgsConstructor
public class DailyReport {
    @Id
    @Column(name="dreport_id", columnDefinition = "CHAR(10)")
    private String dreportId;

    @Column(name = "dreport_date", nullable = false)//, unique = true) // Removed incorrect precision/scale
    private LocalDate dreportDate;

    @Column(name = "dtotal_sales", precision = 12, scale = 2, nullable = false) // Removed unique
    private BigDecimal dtotalSales;

    @Column(name = "dtotal_transac", nullable = false)
    private int dtotalTransac;

    @CreationTimestamp
    @Column(name = "generated_on", nullable = false, updatable = false)
    private LocalDateTime generatedOn;
}