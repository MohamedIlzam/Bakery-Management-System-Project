package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.KodikaraGroupBusinessManagementApplication.model.Driver;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver,String> {
    Optional<Driver> findByName(String dname);
}
