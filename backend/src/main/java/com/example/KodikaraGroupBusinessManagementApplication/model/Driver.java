package com.example.KodikaraGroupBusinessManagementApplication.model;


import jakarta.persistence.*;

@Entity
@Table(name = "driver")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "driver_id",columnDefinition = "CHAR()")
    private String driverId;
    @Column(name = "dname",nullable = false)
    private String name;

    public void setId(String driverId) {
        this.driverId = driverId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return driverId;
    }
    public String getgetDriverNameName() {
        return name;
    }
}




//CREATE TABLE driver (
//        driver_id CHAR(10) NOT NULL,
//dname VARCHAR(50) NOT NULL,
//contact VARCHAR(15),
//PRIMARY KEY (driver_id)
//);