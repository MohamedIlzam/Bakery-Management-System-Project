package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name="dreport_id",columnDefinition = "CHAR(10)")
    private String dreportId;

    @Column(name = "dreport_date",precision = 10, scale = 2, nullable = false)
    private LocalDate dreportDate;

    @Column(name = "dtotal_sales",nullable = false,unique = true)
    private BigDecimal dtotalSales;
    @Column(name = "dtotal_transac", nullable = false)
    private int dtotalTransac;
    @CreationTimestamp
    @Column(name = "generated_on",columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime generatedOn;

    public String getDreportId() {
        return dreportId;
    }

    public LocalDate getDreportDate() {
        return dreportDate;
    }

    public BigDecimal getDtotalSales() {
        return dtotalSales;
    }

    public int getDtotalTransac() {
        return dtotalTransac;
    }
    public void setDtotalTransac(Integer dtotalTransac) {
        this.dtotalTransac = dtotalTransac;
    }
    public LocalDateTime getGeneratedOn() {
        return generatedOn;
    }


}
