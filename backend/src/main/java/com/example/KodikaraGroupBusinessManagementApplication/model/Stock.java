package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "stock")
@Getter
@Setter
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Integer stockId;

    @ManyToOne
    @JoinColumn(name = "pro_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "morning_quantity")
    private Integer morningQuantity;

    @Column(name = "closing_quantity")
    private Integer closingQuantity;
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