package com.example.KodikaraGroupBusinessManagementApplication.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;


@Entity
@Table(name = "product")
public class Product {
    @Id
    private String proId;
    private String name;
    private String category;
    private BigDecimal unitPrice;
    private String status;

}
