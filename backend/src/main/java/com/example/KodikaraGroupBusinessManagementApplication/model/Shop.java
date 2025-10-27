package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Shop")
public class Shop {
    @Id
    @Column(name = "shop_id", columnDefinition = "CHAR(10)")
    private String shopId;

    @Column(name = "shop_name", length = 100, nullable = false)
    private String shopName;

    @Column(name = "owner_name", length = 100, nullable = false)
    private String ownerName;

    @Column(name = "contact_details", length = 15, nullable = false)
    private String contactNo;

    @Column(name = "location", length = 25, nullable = false)
    private String address;
    public SaleDetail getSaleDetailBySaleId(String saleId) {
       return  new SaleDetail();
    }
    public String getShopId() {
        return shopId;
    }
    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
    public String getShopName() {
        return shopName;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public String getContactNo() {
        return contactNo;
    }
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

}





//CREATE TABLE shop(
//        shop_id CHAR(7) NOT NULL,
//name VARCHAR(25) NOT NULL,
//location VARCHAR(25) NOT NULL,
//contact_detatils VARCHAR(15) NOT NULL,
//PRIMARY KEY(shop_id)
//);