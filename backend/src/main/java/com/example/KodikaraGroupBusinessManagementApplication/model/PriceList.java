package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Table(name = "price_list")
@Getter
public class PriceList {
    @Id
    @Column(name = "pricelist_id",columnDefinition = "CHAR(7)")
    private String PlistId;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "pro_id")
    private Product product;

    @Column(name = "price")
    private BigDecimal price;
}








//CREATE TABLE price_list(
//        pricelist_id CHAR(7) NOT NULL,
//shop_id CHAR(7) NOT NULL,
//pro_id CHAR(7) NOT NULL,
//vehicle_id CHAR(7) NOT NULL,
//price DECIMAL(10,2) NOT NULL,
//PRIMARY KEY (pricelist_id),
//FOREIGN KEY (shop_id) REFERENCES shop(shop_id) ON DELETE CASCADE,
//FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id) ON DELETE CASCADE,
//FOREIGN KEY (pro_id) REFERENCES product(pro_id) ON DELETE CASCADE);