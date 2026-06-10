package com.example.KodikaraGroupBusinessManagementApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="vehicle")
public class Vehicle {
    @Id
    @Column(name = "vehicle_id", columnDefinition = "CHAR(10)")
    private String vehicleId;
    @Column(name = "vehicle_no", length = 20, nullable = false, unique = true)
    private String vehicleNo;

    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    public String getVehicleId() {
        return vehicleId;
    }
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
    public String getVehicleNo() {
        return vehicleNo;
    }
    public String getVehicleType() {
        return vehicleType;
    }

    public String getId() {
        return vehicleId;
    }
}
