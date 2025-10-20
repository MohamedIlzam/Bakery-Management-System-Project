package com.example.KodikaraGroupBusinessManagementApplication.DTO;

import java.math.BigDecimal;

public class ProductDTO {

    private String proId;
    private String name;
    private String category;
    private BigDecimal unitPrice;
    // --- getters & setters ---
    public String getProId() {
        return proId;
    }
    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}

