package com.example.KodikaraGroupBusinessManagementApplication.model;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_detail")
public class SaleDetail {
    @Id
    @Column(name="sdetail_id", length = 10)
    private String saleId;

    @ManyToOne
    @JoinColumn(name = "sale_id",nullable = false)
    private Sale sale;

    @ManyToOne
    @JoinColumn(name="pro_id",nullable = false)
    private Product product;

    @Column(name="qty",nullable = false)
    private int qty;
    @Column(name = "unit_price", precision = 10, scale = 2,nullable = false)
    private BigDecimal unitPrice;
    @Column(name = "sub_tot",precision = 10, scale = 2, nullable = false)
    private BigDecimal subTot;

    public String getSaleId() {
        return saleId;
    }
    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }
    public Sale getSale() {
        return sale;
    }
    public void setSale(Sale sale) {
        this.sale = sale;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public int getQty() {
        return qty;
    }
    public void setQty(int qty) {
        this.qty = qty;
    }
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    public BigDecimal getSubTot() {
        return subTot;
    }
    public void setSubTot(BigDecimal subTot) {
        this.subTot = subTot;
    }





}
