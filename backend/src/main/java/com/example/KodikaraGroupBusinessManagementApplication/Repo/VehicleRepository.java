package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Add annotation
public interface VehicleRepository extends JpaRepository<Vehicle,String> {
    Optional<Vehicle> findByVehicleNo(String vehicleNo);
}