package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.Repo.DriverRepository;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.model.Driver;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverService {

    private final DriverRepository driverRepository;

    public Driver createDriver(Driver driver) {
        if (driver.getName() == null || driver.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be empty");
        }
        driver.setId(IdGenerator.driverId());
        if (driver.getContact() != null) {
            driver.setContact(driver.getContact().trim());
        }
        driver.setName(driver.getName().trim());
        return driverRepository.save(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver updateDriver(String id, Driver driverDetails) {
        if (driverDetails.getName() == null || driverDetails.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be empty");
        }
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        driver.setName(driverDetails.getName().trim());
        if (driverDetails.getContact() != null) {
            driver.setContact(driverDetails.getContact().trim());
        } else {
            driver.setContact(null);
        }
        return driverRepository.save(driver);
    }

    public void deleteDriver(String id) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Driver not found");
        }
        driverRepository.deleteById(id);
    }
}
