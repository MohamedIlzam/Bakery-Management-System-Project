package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.Repo.VehicleRepository;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.Vehicle;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private static final List<String> VALID_TYPES = Arrays.asList("Three-Wheel", "Lorry", "Van");

    public Vehicle createVehicle(Vehicle vehicle) {
        validateVehicle(vehicle);
        
        // Check if vehicle number already exists
        if (vehicleRepository.findByVehicleNo(vehicle.getVehicleNo().trim()).isPresent()) {
            throw new IllegalArgumentException("Vehicle number already exists");
        }

        vehicle.setVehicleId(IdGenerator.vehicleId());
        vehicle.setVehicleNo(vehicle.getVehicleNo().trim());
        vehicle.setVehicleType(standardizeVehicleType(vehicle.getVehicleType()));
        if (vehicle.getDriverName() != null) {
            vehicle.setDriverName(vehicle.getDriverName().trim());
        }
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle updateVehicle(String id, Vehicle vehicleDetails) {
        validateVehicle(vehicleDetails);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        // Check if vehicle number changed and is already taken
        String newNo = vehicleDetails.getVehicleNo().trim();
        if (!vehicle.getVehicleNo().equals(newNo) && vehicleRepository.findByVehicleNo(newNo).isPresent()) {
            throw new IllegalArgumentException("Vehicle number already exists");
        }

        vehicle.setVehicleNo(newNo);
        vehicle.setVehicleType(standardizeVehicleType(vehicleDetails.getVehicleType()));
        
        if (vehicleDetails.getDriverName() != null && !vehicleDetails.getDriverName().trim().isEmpty()) {
            vehicle.setDriverName(vehicleDetails.getDriverName().trim());
        } else {
            vehicle.setDriverName(null);
        }
        
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(String id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found");
        }
        vehicleRepository.deleteById(id);
    }

    private void validateVehicle(Vehicle vehicle) {
        if (vehicle.getVehicleNo() == null || vehicle.getVehicleNo().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle number cannot be empty");
        }
        if (vehicle.getVehicleType() == null || vehicle.getVehicleType().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle type cannot be empty");
        }

        String standardizedType = standardizeVehicleType(vehicle.getVehicleType());
        if (!VALID_TYPES.contains(standardizedType)) {
            throw new IllegalArgumentException("Vehicle type must be either Three-Wheel, Lorry, or Van");
        }
    }

    private String standardizeVehicleType(String type) {
        if (type == null) return null;
        String t = type.trim();
        if (t.equalsIgnoreCase("three-wheel") || t.equalsIgnoreCase("threewheel")) {
            return "Three-Wheel";
        }
        if (t.equalsIgnoreCase("lorry")) {
            return "Lorry";
        }
        if (t.equalsIgnoreCase("van")) {
            return "Van";
        }
        return t; // will fail validation
    }
}
