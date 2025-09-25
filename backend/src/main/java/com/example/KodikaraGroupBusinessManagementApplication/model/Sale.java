package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Sale")
public class Sale {
    @Id
    @Column(name = "sale_id", length=10)
    private String saleId;

    @ManyToOne
    @JoinColumn(name= "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name ="vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="date")
    private LocalDate date;
    @Column(name="tot_amount",precision = 10,scale = 2,nullable = false)
    private BigDecimal totalAmount;
    @Column(name = "payment_method", length = 20, nullable = false)
    private String paymentMethod;
    @OneToMany(mappedBy = "sale",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<SaleDetail> saleDetails;

    public String getSaleId() {
        return saleId;
    }
    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }
    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public List<SaleDetail> getSaleDetails() {
        return saleDetails;
    }
    public void setSaleDetails(List<SaleDetail> saleDetails) {
        this.saleDetails = saleDetails;
    }



}
