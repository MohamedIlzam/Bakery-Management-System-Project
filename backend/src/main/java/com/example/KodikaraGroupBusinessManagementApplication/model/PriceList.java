package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;

@Entity
@Table(name = "price_list")
public class PriceList {
    @Id
    //@Column(name = "price list id",columnDefinition ="CHAR(7)")
    @Column(name = "pricelist_id",columnDefinition = "CHAR(7)")
    private String PlistId;
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