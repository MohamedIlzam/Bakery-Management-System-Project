package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "daily_report_detail")
public class DailyReportDetail {
    @Id
    @Column(name = "detail_id",columnDefinition = "CHAR(10)")
    private String detailId;


    @ManyToOne
    @JoinColumn(name = "dreport_id", nullable = false)
    private DailyReport dailyReport;

    @ManyToOne
    @JoinColumn (name = "pro_id", nullable = false)
    private Product product;

    @Column(name="total_qty_sold", nullable = false)
    private int totalQtySold;

    @Column(name = "total_revenue", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalRevenue;
}