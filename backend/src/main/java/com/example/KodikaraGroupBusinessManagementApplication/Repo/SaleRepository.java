package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale,String> {
    List<Sale> findBySaleDate(LocalDate saleDate);

    List<Sale> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);

    void deleteBySaleDate(LocalDate saleDate);

    boolean existsBySaleDate(LocalDate saleDate);
    @Query("SELECT s FROM Sale s WHERE s.vehicle.vehicleId = :vehicleId")
    List<Sale> findByVehicle(@Param("vehicleId") String vehicleId);

    @Query("SELECT s FROM Sale s WHERE s.shop.shopId = :shopId")
    List<Sale> findByShop(@Param("shopId") String shopId);

}
