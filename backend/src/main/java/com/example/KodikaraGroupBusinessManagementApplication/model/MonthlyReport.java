package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "monthly_report")
@NoArgsConstructor
public class MonthlyReport {
    @Id
    @Column(name = "mreport_id",columnDefinition = "CHAR(10)")
    private String mreportId;

    @Column(name ="mreport_date", columnDefinition = "CHAR(7)", nullable = false, unique = true)
    private String mreportDate;

    @Column(name = "mtotal_sales", precision=15, scale = 2, nullable = false)
    private BigDecimal mtotalSales;

    @Column(name="mtotal_transac", nullable = false)
    private int mtotalTransac;

    @CreationTimestamp
    @Column(name = "generated_on", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime generatedOn;
}