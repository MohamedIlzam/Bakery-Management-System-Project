package com.example.KodikaraGroupBusinessManagementApplication.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "monthly_report")
public class MonthlyReport {
    @Id
    @Column(name = "mreport_id",columnDefinition = "CHAR(10)")
    private String mreportId;

   @Column(name ="mreport_date",/*precision = 7*/columnDefinition = "CHAR(7)", nullable = false,unique = true)
    private String mreportDate;
    @Column(name = "mtotal_sales",precision=15,scale = 2, nullable = false)
    private BigDecimal mtotalSales;
    @Column(name="mtotal_transac",nullable = false)
    private int mtotalTransac;

    @CreationTimestamp
    //@Column(name = "genarated_on",columnDefinition ="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "generated_on",columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime genaratedOn;

    public String getMreportId() {
        return mreportId;
    }
    public void setMreportId(String mreportId) {
        this.mreportId = mreportId;
    }
    public String getMreportDate() {
        return mreportDate;
    }
    public void setMreportDate(String mreportDate) {
        this.mreportDate = mreportDate;
    }
    public BigDecimal getMtotalSaels() {
        return mtotalSales;
    }
    public void setMtotalSaels(BigDecimal mtotalSaels) {
        this.mtotalSales = mtotalSaels;
    }
    public int getMtotalTransac() {
        return mtotalTransac;
    }
    public void setMtotalTransac(int mtotalTransac) {
        this.mtotalTransac = mtotalTransac;
    }

}


//CREATE TABLE monthly_report(
//        mreport_id CHAR(10) NOT NULL,
//mreport_date CHAR(7)NOT NULL UNIQUE,
//mtotal_sales DECIMAL(15,2) NOT NULL,
//mtotal_transac INT NOT NULL,
//generated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//PRIMARY KEY (mreport_id));