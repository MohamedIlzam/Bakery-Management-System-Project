package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale,String> {
    // These methods now match the 'saleDate' field in the Sale entity
    List<Sale> findBySaleDate(LocalDate saleDate);
    List<Sale> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);
    void deleteBySaleDate(LocalDate saleDate);
    boolean existsBySaleDate(LocalDate saleDate);

    // Kept these for analytics filtering, but removed @Query
    List<Sale> findByVehicleVehicleId(String vehicleId);
    List<Sale> findByShopShopId(String shopId);
    @Query("SELECT s FROM Sale s LEFT JOIN s.vehicle v LEFT JOIN s.shop sh LEFT JOIN s.driver d " +
            "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
            "AND (:vehicleNo IS NULL OR v.vehicleNo = :vehicleNo) " +
            "AND (:shopName IS NULL OR sh.shopName = :shopName) " +
            "AND (:driverName IS NULL OR d.name = :driverName)")
    List<Sale> findSalesByCriteria(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("vehicleNo") String vehicleNo,
            @Param("shopName") String shopName,
            @Param("driverName") String driverName
    );
}