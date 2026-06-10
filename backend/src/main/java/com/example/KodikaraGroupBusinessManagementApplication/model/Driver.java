package com.example.KodikaraGroupBusinessManagementApplication.model;


import jakarta.persistence.*;

@Entity
@Table(name = "driver")
public class Driver {
    @Id


    @Column(name = "driver_id",columnDefinition = "CHAR(10)")
    private String driverId;
    @Column(name = "dname",nullable = false)
    private String name;
    @Column(name = "contact", length = 15)
    private String contact;

    public void setId(String driverId) {
        this.driverId = driverId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getDriverId() {
        return driverId;
    }
    public String getName() {
        return name;
    }
    public String getContact() {
        return contact;
    }
}




//CREATE TABLE driver (
//        driver_id CHAR(10) NOT NULL,
//dname VARCHAR(50) NOT NULL,
//contact VARCHAR(15),
//PRIMARY KEY (driver_id)
//);