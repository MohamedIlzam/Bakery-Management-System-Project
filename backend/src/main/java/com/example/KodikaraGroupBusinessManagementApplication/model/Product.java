package com.example.KodikaraGroupBusinessManagementApplication.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;


@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name="pro_id",columnDefinition = "CHAR(7)")
    private String proId;
    @Column(name="name",length = 15, nullable = false)
    private String name;
    @Column(name="category",length = 20, nullable = false)
    private String category;
    @Column(name="unit_price",precision=10,scale = 2, nullable = false)
    private BigDecimal unitPrice;
    @Column(name="status",length = 15, nullable = false)
    private String status;

   public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}






//CREATE TABLE product (
//        pro_id CHAR (7) NOT NULL,
//name VARCHAR(15) NOT NULL,
//category VARCHAR(20) ,
//unit_price DECIMAL(10,2) NOT NULL,
//status VARCHAR(15) NOT NULL,
//PRIMARY KEY (pro_id)
//);