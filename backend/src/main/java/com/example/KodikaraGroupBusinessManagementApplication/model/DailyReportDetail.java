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
@JoinColumn (name = "pro_id",nullable = false)
private Product product;

@Column(name="total_qty_sold",nullable = false)
private int quantity_sold;
@Column(name = "total_revenue",precision = 12,scale = 2,nullable = false)
    private BigDecimal totalRevenue;

}





//CREATE TABLE daily_report_detail (
//        detail_id CHAR(10) NOT NULL,
//dreport_id CHAR(10) NOT NULL,
//pro_id CHAR(7) NOT NULL,
//total_qty_sold INT NOT NULL,
//total_revenue DECIMAL(12,2) NOT NULL,
//PRIMARY KEY (detail_id),
//FOREIGN KEY (dreport_id) REFERENCES daily_report(dreport_id) ON DELETE CASCADE,
//FOREIGN KEY (pro_id) REFERENCES product(pro_id)
//        );