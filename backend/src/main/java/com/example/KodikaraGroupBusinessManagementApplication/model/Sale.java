package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "Sale")
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    @Id
    @Column(name = "sale_id", columnDefinition = "CHAR(10)")
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

    @ManyToOne
    @JoinColumn(name="driver_id")
    private Driver driver;

    @CreationTimestamp
    @Column(name = "saleDate")
    private LocalDate saleDate;
    @Column(name="tot_amount",precision = 10,scale = 2,nullable = false)
    private BigDecimal totalAmount;
    @Column(name = "payment_method", length = 20, nullable = false)
    private String paymentMethod;
    @OneToMany(mappedBy = "sale",cascade = CascadeType.ALL)
    private List<SaleDetail> saleDetails;
    @CreationTimestamp
//    @Column(name = "date", nullable = false, updatable = false)
//    private LocalDateTime saleDate;

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
    public void setDate(LocalDate now) {
        this.saleDate = now;
    }
    public Object getSaleDate() {
        return saleDate;
    }
    public void setSaleDate(LocalDate now) {
        this.saleDate = now;
    }
    public Driver gerDriver(){
        return driver;
    }
    public void setDriver(){
        this.driver=driver;
    }
}








//CREATE TABLE sale(
//        sale_id CHAR(10) NOT NULL,
//shop_id CHAR(7),
//vehicle_id CHAR(7),
//date DATE,
//tot_amount DECIMAL(10,2) NOT NULL,
//payment_method varchar(20) NOT NULL,
//PRIMARY KEY (sale_id),
//FOREIGN KEY (shop_id) REFERENCES shop(shop_id) ON DELETE CASCADE,
//FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id) ON DELETE CASCADE);
//ALTER TABLE sale
//ADD user_id VARCHAR(15),
//ADD CONSTRAINT fk_sale_user FOREIGN KEY (user_id) REFERENCES user(user_id);