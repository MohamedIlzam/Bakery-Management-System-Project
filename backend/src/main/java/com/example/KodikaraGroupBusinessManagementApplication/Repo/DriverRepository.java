package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.KodikaraGroupBusinessManagementApplication.model.Driver;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver,String> {
    Optional<Driver> findByName(String name);
}