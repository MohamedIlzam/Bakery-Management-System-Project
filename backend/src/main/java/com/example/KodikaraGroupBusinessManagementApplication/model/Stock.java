package com.example.KodikaraGroupBusinessManagementApplication.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @Column(name = "stock_id",columnDefinition = "CHAR(7)")
    private String stockId;
}





//CREATE TABLE stock(
//        stock_id CHAR(7) NOT NULL,
//pro_id CHAR(7),
//date DATE,
//open_qty INT,
//qty_sold INT NOT NULL,
//qty_returned INT,
//closing_qty INT,
//PRIMARY KEY(stock_id),
//FOREIGN KEY(pro_id) REFERENCES product(pro_id) ON DELETE CASCADE);